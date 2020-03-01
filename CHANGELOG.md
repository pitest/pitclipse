# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [2.1.0] - 2020-03-01
### Added
 - [#73](https://github.com/pitest/pitclipse/pull/113) Support for JUnit 5 tests

### Removed
 - [#113](https://github.com/pitest/pitclipse/pull/113) Support for Eclipse Mars, Eclipse Neon

## [2.0.2] - 2020-02-18
### Changed
- [#74](https://github.com/pitest/pitclipse/issues/74) Prevent the dependencies of analyzed projects from messing up with Pitest
- [#107](https://github.com/pitest/pitclipse/issues/107) Upgrade Pitest to 1.4.11

## [2.0.1] - 2020-02-06
### Added
- [#76](https://github.com/pitest/pitclipse/issues/76) Properly express dependencies between Pitclipse's features to prevent installing "UI" without "Core"
- [#91](https://github.com/pitest/pitclipse/issues/91) Exclude `org.apache.logging.log4j` from Pitest's analysis by default

## [2.0.0] - 2019-10-05
### Fixed
- [#72](https://github.com/pitest/pitclipse/issues/72) Make Pitclipse work on Mars 2 and higher
- Support projects using Java 9+ (only if the project does not use modules)

### Added
- [#80](https://github.com/pitest/pitclipse/pull/80) Contribution guide helping contributors to setup and make changes to the project

### Changed
- Pitclipse is now released at [https://dl.bintray.com/kazejiyu/Pitclipse/updates/](https://dl.bintray.com/kazejiyu/Pitclipse/updates/)
- [#80](https://github.com/pitest/pitclipse/pull/80) Upgrade Pitest to 1.4.6
- [#80](https://github.com/pitest/pitclipse/pull/80) Use Java 1.8

## [1.1.4] - 2016-04-19
### Added
- Run Pitest on projects from _Project Explorer_'s context menu
- Run Pitest from a dedicated Run Configuration
- [#61](https://github.com/pitest/pitclipse/issues/61) Use Pitest 1.1.7