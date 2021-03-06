package org.jenkinsci.plugins.incredibuild;
import hudson.Launcher;
import hudson.Extension;
import hudson.Proc;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.incredibuild.Commands.BuildConsoleCommand;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link IncredibuildBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #path})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class IncredibuildBuilder extends Builder {

    private final String path;
    private final String configuration;
    private final String project;
    private final Boolean wait;
    private final Boolean rebuild;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public IncredibuildBuilder(String path, String configuration, String project, Boolean all, Boolean wait, Boolean rebuild, Boolean incredilink) {
        this.path = path;
        this.configuration = configuration;
        this.project = project;
        this.wait = wait;
        this.rebuild = rebuild;
    }

    public Boolean getWait() {
        return wait;
    }

    public Boolean getRebuild() {
        return rebuild;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getPath() {
        return path;
    }

    public String getProject() {
        return project;
    }

    public String getConfiguration() {
        return configuration;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
        try {
            listener.getLogger().println("Running an Incredibuild build");
            BuildConsoleCommand buildConsoleCommand = new BuildConsoleCommand(getConfiguration(), getProject(), getPath(), getDescriptor().getBuildConsolePath(), getWait(), getRebuild());
            listener.getLogger().println("Solution: " + getPath());
            listener.getLogger().println("Project: " + getProject());
            listener.getLogger().println("Configuration: " + getConfiguration());
            listener.getLogger().println("Wait: " + getWait());
            listener.getLogger().println("Rebuild: " + getRebuild());

            Launcher.ProcStarter ps = launcher.new ProcStarter();
            ps = ps.cmds(buildConsoleCommand.getArguments()).stdout(listener);
            ps = ps.pwd(build.getWorkspace()).envs(build.getEnvironment(listener));
            Proc proc = launcher.launch(ps);
            int retcode = proc.join();
            if (retcode != 0) {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link IncredibuildBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/IncredibuildBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */


    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */

        private String buildconsolepath;

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'Path'.
         *
         * @param value
         *            This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the
         *         browser.
         */
        public FormValidation doCheckPath(@QueryParameter String value) throws IOException, ServletException {
            if (value.isEmpty()) {
                return FormValidation.error("Path to Solution is required.");
            }
            return FormValidation.ok();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Build a project with Incredibuild";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            buildconsolepath = formData.getString("buildconsolepath");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public String getBuildConsolePath() {
            return buildconsolepath;
        }
    }
}

