# OpenAPI spec

This project has a REST api with an integrated Swagger and therefore can produce
an OpenAPI Specification JSON file.

## Location
The current specification can be found in this folder, called `elit-openapi.json`.

Since an openapi definition is not enough to perform HTTP requests, we also provide
a file containing a Postman collection, if you care about this format. Most
of the HTTP clients can import from OpenAPI but some don't and require a Postman 
collection or whatever. The Postman collection is named `OpenAPI definition.postman_collection.json`.

## Update 
When the application will evolve in the future, make sure to update the OpenAPI specification file.
Currently, the application uses Swagger and it can generate OpenAPI specification.

To generate the specification you can go to:

``
http://server:port/context-path/v3/api-docs
``

This is a general format for Swagger, and thus for this project the actual URL is:

``
http://localhost:8080/api/v3/api-docs
``

Make sure to update those values according to values in application.propeties files !

This URL will produce a json response displayed in your browser. Select all text and replace the
contents of `elit-openapi.json` file with it. You can then regenerate the Postman collection
if it pleases you or import into other tools ;)
