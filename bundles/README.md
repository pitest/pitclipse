# Pitclipse Architecture <!-- omit in toc -->

- [Overview](#Overview)
- [Event flow](#Event-flow)
- [How can I be notified when a new PIT application is launched?](#How-can-I-be-notified-when-a-new-PIT-application-is-launched)
- [How do I listen for new PIT results?](#How-do-I-listen-for-new-PIT-results)
- [How do I listen for new PIT mutations results?](#How-do-I-listen-for-new-PIT-mutations-results)

## Overview

This folder defines Pitclipse's source plug-ins. Here is an overview of their purposes.

Bundle | Purpose
------ | -------
`org.pitest` | Wraps Pitest JAR as an Eclipse plug-in
`org.pitest.pitclipse.core` | Defines extension points and handlers forwarding Pitest results to registered extensions.
`org.pitest.pitclipse.launch` | Provides a PIT Mutation launch configuration.
`org.pitest.pitclipse.launch.ui` | Integrates Pitclipse's launch configurations within Eclipse IDE's UI.
`org.pitest.pitclipse.listeners` | Fragment for `org.pitest` allowing Pitest to discover Pitclipse's mutation listeners.
`org.pitest.pitclipse.preferences.ui` | Provides a Pitclipse Preferences page.
`org.pitest.pitclipse.runner` | Allows to execute Pitest within an Eclipse runtime.
`org.pitest.pitclipse.ui` | Provides views to display Pitest's results.

## Event flow

Here is an introduction to how Pitclipse works.

> **TODO**: Insert a sequence diagram to make the flow clearer

1. A new Pitest application is launched through an Eclipse [Launch Configuration](https://www.vogella.com/tutorials/EclipseLauncherFramework/article.html). This Launch Configuration executes Pitest in a background VM.
   - See [AbstractPitLaunchDelegate](org.pitest.pitclipse.launch/src/org/pitest/pitclipse/launch/AbstractPitLaunchDelegate.java) for the definition of the Launch Configuration
   - See [PitRunner](org.pitest.pitclipse.runner/src/org/pitest/pitclipse/runner/PitRunner.java) which `main` method is executed within the background VM and actually launches PIT
2. The Launch Configuration uses an instance of [ExtensionPointHandler](org.pitest.pitclipse.core/src/org/pitest/pitclipse/core/extension/handler/ExtensionPointHandler.java) to inform listeners that a PIT application has been launched
3. A [PitExecutionNotifier](org.pitest.pitclipse.core/src/org/pitest/pitclipse/core/launch/PitExecutionNotifier.java) is thus notified and launches a server in background to listen for PIT results
4. Once PIT results are available, they are broadcasted through an [ExtensionPointResultHandler](org.pitest.pitclipse.core/src/org/pitest/pitclipse/core/launch/ExtensionPointResultHandler.java)
5. A [MutationsModelNotifier](org.pitest.pitclipse.core/src/org/pitest/pitclipse/core/result/MutationsModelNotifier.java) is thus notified; it extracts the information about detected information and then broadcast them
6. The _PIT Mutations_ and _PIT Summary_ views are thus notified and updated with PIT results.

## How can I be notified when a new PIT application is launched?

A new listener can be registered by contributing to the `org.pitest.pitclipse.core.executePit` extension point. It only requires a class that implements the `ResultNotifier<PitRuntimeOptions>` interface:

```java
public class MyPitStartListener implements ResultNotifier<PitRuntimeOptions> {

    @Override
    public void handleResults(PitRuntimeOptions options) {
        // process options...
    }

}
```

## How do I listen for new PIT results?

A new listener can be registered by contributing to the `org.pitest.pitclipse.core.results` extension point. It only requires a class that implements the `ResultNotifier<PitResults>` interface:

```java
public class MyPitResultsListener implements ResultNotifier<PitResults> {

    @Override
    public void handleResults(PitResults results) {
        // process results...
    }

}
```

## How do I listen for new PIT mutations results?

A new listener can be registered by contributing to the `org.pitest.pitclipse.core.mutations.results` extension point. It only requires a class that implements the `ResultNotifier<MutationsModel>` interface:

```java
public class MyPitMutationsResultsListener implements ResultNotifier<MutationsModel> {

    @Override
    public void handleResults(MutationsModel mutations) {
        // process detected mutations...
    }

}
```