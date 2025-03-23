# Rijksmuseum API Tests

### Description
This project is designed to validate the functionality of RESTful APIs using **Rest-Assured**, a popular Java library for API test automation. The project includes various test cases to ensure the reliability, correctness, and robustness of the API endpoints.

---

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)
- [Running Tests](#running-tests)
- [Test Scenarios](#test-scenarios)
- [Folder Structure](#folder-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- **Validation of API Responses**: Checks for correct response codes, body, and headers.
- **Dynamic Query Testing**: Handles query parameters like `facetType` and `involvedMaker`.
- **Error Handling Tests**: Ensures APIs return proper error codes for invalid parameters.
- **End-to-End Test Cases**:
    - Validate `facetType` fields in the collection responses.
    - Check the filtering functionality of individual endpoints (`GET` with query parameters).
- **Built with Best Practices in Test Automation** using **Rest-Assured** and **JUnit**.

---

## Technologies Used
- **Java 21**: The programming language for building the test cases.
- **Rest-Assured**: Java library for testing REST APIs.
- **JUnit**: Test framework for writing and running test cases.
- **Maven**: Dependency management and build tool.

---

## Setup Instructions

### Pre-requisites
- Install **Java 21** and ensure it’s set up in your system.
- Install **Maven** or **Gradle** for dependency management.
- Clone this repository:
    ```bash
    git clone <repository-url>
    ```
- Go to the project directory:
    ```bash
    cd <project-folder>
    ```

### Install Dependencies:
Run the following command to install the dependencies:
```bash
mvn clean install
```

---

## Running Tests

You can run all the test cases using the following command:
```bash
mvn test
```

Alternatively, run specific tests by navigating to the desired test class and executing it through your IDE.

### View Test Reports:
After running the tests, you can find the test reports under:
```plaintext
target/surefire-reports/
```

---

## Test Scenarios

### 1. **Invalid API Key**
- Ensures the API returns a `403 Forbidden` error when an invalid key is provided.

### 2. **Test Retrieval of Collections**
- Test `facetType` field to ensure it:
    - Exists in the response.
    - Is not empty.

### 3. **Test Filtering by `involvedMaker`**
- Retrieve art collections filtered by the maker (e.g., **Rembrandt**).
- Validates successful filtering with:
    - Status code `200`
    - Results containing the requested `involvedMaker`.

### 4. **General API Status Tests**
- Validates the response for different endpoints with:
    - Correct response codes (`200`, `400`, `403`).
    - Proper structure of response body (e.g., `artObjects` list).

### Example Code Ref:
See `src/test/java` for detailed test implementations.

---

## Folder Structure

```plaintext
rest-assured-tests/
│
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   ├── tests/                     # Test classes (e.g., TestRetrieveCollection)
│   │   │   ├── utilities/                # Utility classes (e.g., Helpers, Configurations)
│   │   ├── resources/                    # Configuration and test data files
│
├── README.md                             # Project documentation
├── pom.xml                               # Maven dependencies and build script
```

---

## Contributing

We welcome contributions to improve this project! Follow the steps below:

1. Fork the repository.
2. Create a new feature branch:
    ```bash
    git checkout -b feature/new-feature
    ```
3. Make your changes and commit them:
    ```bash
    git commit -m "Add new feature"
    ```
4. Push the changes:
    ```bash
    git push origin feature/new-feature
    ```
5. Submit a pull request with a detailed explanation of your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.