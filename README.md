# aws-lambda-request-handler
Framework for creating AWS Lambda functions in API Gateway proxy integration mode.

[![Build Status](https://travis-ci.org/kaklakariada/aws-lambda-request-handler.svg?branch=master)](https://travis-ci.org/kaklakariada/aws-lambda-request-handler)
[![Download](https://api.bintray.com/packages/kaklakariada/maven/aws-lambda-request-handler/images/download.svg) ](https://bintray.com/kaklakariada/maven/aws-lambda-request-handler/_latestVersion)

This library maps [input](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy.html#api-gateway-simple-proxy-for-lambda-input-format) and [output](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-set-up-simple-proxy.html#api-gateway-simple-proxy-for-lambda-output-format) format of a Lambda Function for Proxy Integration.

For deploying serverless applications using gradle see https://github.com/kaklakariada/aws-sam-gradle

## Development

```bash
$ git clone https://github.com/kaklakariada/aws-lambda-request-handler.git
$ ./gradlew check
# Test report: build/reports/tests/index.html
```

### Using eclipse

Import into eclipse using [buildship](https://projects.eclipse.org/projects/tools.buildship).

### Generate license header for added files:

```bash
$ ./gradlew licenseFormatMain licenseFormatTest
```

### Publish to jcenter

1. Create file `gradle.properties` in project directory with the following content and enter your bintray account:

    ```properties
    bintrayUser = <user>
    bintrayApiKey = <apiKey>
    ```

2. Increment version number in `build.gradle`, commit and push.
3. Run the following command:

    ```bash
    $ ./gradlew clean build check bintrayUpload -i
    ```

4. Create a new [release](https://github.com/kaklakariada/aws-lambda-request-handler/releases) on GitHub.
5. Sign in at https://bintray.com/ and publish the uploaded artifacts.
