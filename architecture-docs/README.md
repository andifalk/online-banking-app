# Architecture Docs

Sample architecture documentation based on [ARC42](https://arc42.org) asciidoc template including support for UML diagrams with [PlantUML](https://plantuml.com).

## Table of Contents

* [1.Introduction and Goals](src/docs/asciidoc/chapters/01_introduction_and_goals.adoc)
* [2.Architecture Constraints](src/docs/asciidoc/chapters/02_architecture_constraints.adoc)
* [3.Context and Scope](src/docs/asciidoc/chapters/03_context_and_scope.adoc)
* [4.Solution Strategy](src/docs/asciidoc/chapters/04_solution_strategy.adoc)
* [5.Building Block View](src/docs/asciidoc/chapters/05_building_block_view.adoc)
* [6.Runtime View](src/docs/asciidoc/chapters/06_runtime_view.adoc)
* [7.Deployment View](src/docs/asciidoc/chapters/07_deployment_view.adoc)
* [8.Crosscutting Concepts](src/docs/asciidoc/chapters/08_concepts.adoc)
* [9.Architecture Decisions](src/docs/asciidoc/chapters/09_architecture_decisions.adoc)
* [10.Quality Requirements](src/docs/asciidoc/chapters/10_quality_requirements.adoc)
* [11.Risks and Technical Debts](src/docs/asciidoc/chapters/11_technical_risks.adoc)
* [12.Glossary](src/docs/asciidoc/chapters/12_glossary.adoc)

## Prerequisites

You need to install GraphViz first to render UML diagrams.
See [GraphViz Dot](https://plantuml.com/graphviz-dot) for details.

## Build docs

Just perform a `./mvnw clean package`to generate the PDF and EPub3 documentation file.
