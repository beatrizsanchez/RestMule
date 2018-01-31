# RestMule #

A framework for handling various service policies, such as limited number of requests within a period of time and multi-page responses, by generating resilient clients that are able to handle request rate limits, network failures, response caching, and paging in a graceful and transparent manner.

### How do I use a generated resilient client? ###

We have a generated resilient client for GitHub's API v3. You may find it under examples/org.epsilonlabs.restmule.github

This project depends on org.epsilonlabs.restmule.core, so make sure you have it in the same Eclipse Workspace.

### How do I generate a resilient client from an OpenAPI Specification? ###

To generate the GitHub Resilient Client you need to import the EMC-JSON driver into your eclipse plugin and run a new eclipse. In the new eclipse you need to import the org.epsilonlabs.restmule.codegen plugin and execute the run configuration generateFromOAS.launch which will generate the code for GitHub's OAS.

### Project Info ###
* Version 1.0.0
