package org.jenkins.plugins.lockableresources;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.cli.CLIAction;
import hudson.model.*;
import hudson.tasks.BatchFile;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import jenkins.security.ConfidentialStore;
import jenkins.security.DefaultConfidentialStore;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class FreeStyleBuildTest {

	@Rule
	public RestartableJenkinsRule story = new RestartableJenkinsRule();

	@ClassRule
	public static BuildWatcher buildWatcher = new BuildWatcher();

	@Test
	public void interoperability() {
		final Semaphore semaphore = new Semaphore(1);
		story.addStep(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				LockableResourcesManager.get().createResource("resource1");

				final FreeStyleProject f = story.j.createFreeStyleProject("f");
				f.addProperty(new RequiredResourcesProperty("resource1", null, null, null));
				f.getBuildersList().add(new BatchFile("echo hello") {
					@Override
					public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException {
						FilePath ws = build.getWorkspace();
						try {
							FilePath script = createScriptFile(ws);
							EnvVars envVars = new EnvVars();
							if (ws != null) {
								envVars.put("WORKSPACE", ws.getRemote());
							}

							f.getScm().buildEnvVars(build, envVars);
							Iterator i$ = build.getEnvironments().iterator();
							while (i$.hasNext()) {
								Environment e = (Environment) i$.next();
								e.buildEnvVars(envVars);
							}

			    i$ = build.getActions(EnvironmentContributingAction.class).iterator();

			    while(i$.hasNext()) {
			      EnvironmentContributingAction a = (EnvironmentContributingAction)i$.next();
			      a.buildEnvVars(build, envVars);
			    }
							envVars.put("CLASSPATH", "");
			    i$ = EnvironmentContributor.all().reverseView().iterator();

			    while(i$.hasNext()) {
			      EnvironmentContributor ec = (EnvironmentContributor)i$.next();
			      ec.buildEnvironmentFor(build, envVars, listener);
			    }



			    EnvVars.resolve(envVars);
							i$ = build.getBuildVariables().entrySet().iterator();

							while (i$.hasNext()) {
								Map.Entry<String, String> e = (Map.Entry) i$.next();
								envVars.put((String) e.getKey(), (String) e.getValue());
							}


							int r = join(launcher.launch().cmds(this.buildCommandLine(script)).envs(envVars).stdout(listener).pwd(ws).start());
													} catch (IOException e) {
							throw new RuntimeException(e.getMessage());
						}
						return true;
					}
					private Object writeReplace() {
			    return new Object();
			  }
				});
//				f.getBuildersList().add(new BatchFile("echo hello"));
				semaphore.acquire();
				f.scheduleBuild2(0).waitForStart();

				semaphore.release();

				// Wait for lock after the freestyle finishes
//				story.j.waitForMessage("Lock released on resource [resource1]", b1);
			}
		});
	}

}
