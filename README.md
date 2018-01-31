# RestMule #

A framework for handling various service policies, such as limited number of requests within a period of time and multi-page responses, by generating resilient clients that are able to handle request rate limits, network failures, response caching, and paging in a graceful and transparent manner.

### Setup ###
1. Please ensure you run `setupCore.launch` in `restmule.core`
2. To use github's generated client, ensure you run `setupGitHub.launch` in `restmule.github`

### How do I use a generated resilient client? ###

We have a generated resilient client for GitHub's API v3. You may find it under `examples/restmule.github`

This project depends on `restmule.core`, so make sure you have it in the same Eclipse Workspace.

### How do I generate a resilient client from an OpenAPI Specification? ###

To generate the GitHub Resilient Client you need to import the `dependencies/emc-json` driver into your eclipse workspace and run a new eclipse instance. In the new eclipse you need to import the `restmule.codegen` plugin and execute the run configuration `generateFromOAS.launch` which will generate the code from GitHub's OAS found in the schemas` folder.

### Project Info ###
* Version 1.0.0
