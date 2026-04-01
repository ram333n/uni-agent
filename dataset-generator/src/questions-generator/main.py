import os

from langchain_community.document_loaders import DirectoryLoader, Docx2txtLoader, PyMuPDFLoader
from dotenv import load_dotenv, find_dotenv

load_dotenv(find_dotenv())


def main():
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

    documents.extend(docx_loader.load())
    documents.extend(pdf_loader.load())
    print(len(documents))

if __name__ == '__main__':
    main()
