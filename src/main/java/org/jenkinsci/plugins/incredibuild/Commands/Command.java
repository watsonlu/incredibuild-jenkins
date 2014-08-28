package org.jenkinsci.plugins.incredibuild.Commands;

import hudson.util.ArgumentListBuilder;

/**
 * Created by lwatson on 8/28/2014.
 */
public interface Command {

    ArgumentListBuilder getArguments();
}
