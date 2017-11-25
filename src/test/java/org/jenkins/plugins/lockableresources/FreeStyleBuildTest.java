package org.jenkins.plugins.lockableresources;

import hudson.Launcher;
import hudson.cli.CLIAction;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
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

				FreeStyleProject f = story.j.createFreeStyleProject("f");
				f.addProperty(new RequiredResourcesProperty("resource1", null, null, null));
				f.getBuildersList().add(new Shell("echo Freestyle test"));
				f.getBuildersList().add(new TestBuilder() {

					@Override
					public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
						semaphore.acquire();
						return true;
					}

				});
				semaphore.acquire();
				f.scheduleBuild2(0).waitForStart();

				semaphore.release();

				// Wait for lock after the freestyle finishes
//				story.j.waitForMessage("Lock released on resource [resource1]", b1);
			}
		});
	}

}
