import asyncio
import os

from langchain_community.document_loaders import DirectoryLoader, Docx2txtLoader, PyMuPDFLoader
from dotenv import load_dotenv, find_dotenv
from langchain_community.embeddings import OllamaEmbeddings
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_groq import ChatGroq
from ragas import RunConfig
from ragas.embeddings import LangchainEmbeddingsWrapper
from ragas.llms import LangchainLLMWrapper
from ragas.testset import TestsetGenerator
from ragas.testset.synthesizers.single_hop.specific import SingleHopSpecificQuerySynthesizer
from ragas.testset.synthesizers.multi_hop.abstract import MultiHopAbstractQuerySynthesizer
from ragas.testset.synthesizers.multi_hop.specific import MultiHopSpecificQuerySynthesizer

load_dotenv(find_dotenv())

def load_documents():
    documents_dir = os.environ["DOCUMENTS_DIR"]

    docx_loader = DirectoryLoader(
        path=documents_dir,
        glob="**/*.docx",
        loader_cls=Docx2txtLoader,
        silent_errors=True,
        show_progress=True
    )

    pdf_loader = DirectoryLoader(
        path=documents_dir,
        glob="**/*.pdf",
        loader_cls=PyMuPDFLoader,
        silent_errors=True,
        show_progress=True
    )

    documents = []
    # documents.extend(docx_loader.load())
    documents.extend(pdf_loader.load())

    # documents = [doc for doc in documents if len(doc.page_content.strip()) > 500] TODO: test pipeline, adjust limits
    # documents = documents[:5]

    return documents


async def adapt_synthesizers(synthesizers, llm):
    for synthesizer, _ in synthesizers:
        prompts = await synthesizer.adapt_prompts("ukrainian", llm=llm)
        synthesizer.set_prompts(**prompts)


def main():
    llm = LangchainLLMWrapper(
        ChatGoogleGenerativeAI(model="gemini-2.5-flash", api_key=os.environ["GOOGLE_API_KEY"])
    )
    embedding_model = LangchainEmbeddingsWrapper(
        OllamaEmbeddings(model="qwen3-embedding:0.6b")
    )

    synthesizers = [
        (SingleHopSpecificQuerySynthesizer(llm=llm), 0.6),
        (MultiHopSpecificQuerySynthesizer(llm=llm), 0.2),
        (MultiHopAbstractQuerySynthesizer(llm=llm), 0.2),
    ]

    asyncio.run(adapt_synthesizers(synthesizers, llm))

    run_config = RunConfig(
        max_retries=20,
        max_wait=120,
        timeout=180,
        max_workers=1,
    )

    generator = TestsetGenerator(llm=llm, embedding_model=embedding_model)
    documents = load_documents()
    dataset = generator.generate_with_langchain_docs(
        documents,
        testset_size=10,
        query_distribution=synthesizers,
        run_config=run_config
    )

    print(dataset)

    df = dataset.to_pandas()
    df.to_csv("testset.csv", index=False)
    print(f"Saved {len(df)} questions to testset.csv")


if __name__ == "__main__":
    main()