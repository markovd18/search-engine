# Search Engine

This application is a simple search engine that allows one to index documents in an expected format and search for them using text querries. Search engine is extended with a web crawler that crawls default website after booting up if needed and stores articles to be indexed.

## Usage

The executable can be executed simply by running 
```bash
java -jar search-engine.jar [-OPTION]
```

Recognized CLI options are:
- `-h (--help)` - prints usage info and quits
- `-s (--storage) <memory/file>` - specifies a storage implementation to be used - in-memory or file-based (default being the file-based implementation)
  - NOTE: in-memory storage implementation is indended only for development purposes and should not be used for real application running; also, not all interface methods may be supported by the in-memory implementation

### Requirements
This application requires `jre-17` or newer to be executed.

## User interface
After starting-up, user is presented with a command-line interface that allows him to enter commands to operate the search engine.

Supported commands are:
- `clear` - clears the screen
- `exit` - quits the application
- `query <query string> [--model <boolean/vector>]` - querries the index for documents matching given query string
    - optional argument `--model` allows to specify which search model should be used; currently supported search models are 'Boolean search model' and 'Vector model' (default value being `vector`)

## How it works
### 1. Checking the index data
After booting up, the application checks if there is any data present in the index.
- NOTE: by the time of writing this, the only supported index implementation is in-memory implementation, therefore the index is always empty on startup. However, this step was mainly introduced due to the possibility of adding a file-based index which persists data between individual runs.

### 2. Checking the `storage` data
If there is data in the index, application presents the user with an interface. If not, a `storage` is checked if it holds any entries. `Storage` is a file-based "database" that holds entries of articles that may be loaded and indexed. If there is data in the storage, all entries are loaded into memory and indexed.

### 3. Checking the `url storage` data
If there are no entries in the `storage`, the documents are then set to be crawled from the Internet and indexed. For that, the [Hokej.cz](https://hokej.cz) website is crawled for all urls of the latest articles which should then be downloaded and indexed.

To reduce the overhead of crawling the urls in every run, those are stored in the `url storage` in a file. If the file is present, urls are loaded and articles are crawled. If not, the homepage is crawled first, urls are stored for future runs and then the articles are crawled.

### 4. Presenting the interface
After all the previous steps, the user is presented with the previously mentioned CLI that allows them to interact with the application.

## Building from sources
To build the executable from sources, set the working directory to the root of the project and run

```bash
mvn clean package
```
This builds all maven sub-modules and creates an executable `.jar` file in the `core/target` directory. It will be called `search-engine-<version>-jar-with-dependencies.jar`. This executable may then be executed as mentioned before.

### Requirements
Requirements to build from sources are `maven` and `jdk-17` or newer installed.

## Modules
Implementation is separated into individual modules which are responsible for different use cases and logic.

### Utils
`Search Engine Utils` module consists of set of classes that contain utility functions for validation, working with links or files and shared classes which are required by other modules such as `Storage` or `FileLoader`.

### Crawler
`Search Engine Crawler` module is responsible for crawling the web and downloading urls to be processed for articles and storing crawled articles into `storage` to be later indexed.

### Core
`Search Engine Core` is the fundamental module of the application. It contains all startup logic as well as the indexing and querrying logic. It knows how to read the `storage` data, load it into a memory and index it. CLI is also present in this module.

*This project was developed for purposes of the KIV/IR subject of Faculty of applied sciences of University of West Bohemia in Pilesn.*