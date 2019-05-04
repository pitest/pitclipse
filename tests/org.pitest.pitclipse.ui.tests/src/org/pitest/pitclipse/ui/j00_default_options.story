Given eclipse opens and the welcome screen is acknowledged
And the java perspective is opened
And an empty workspace

Then the project level scope preference is selected
And the mutation tests run in parallel preference is selected
And the use incremental analysis preference is not selected
And the excluded classes preference is not set
And the excluded methods preference is not set
And the avoid calls to preference is set to the PIT defaults
And the default mutators preference is selected
And the default timeout is 3000
And the default timeout factor is 1.25 