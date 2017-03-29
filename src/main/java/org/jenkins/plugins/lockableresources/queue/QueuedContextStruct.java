/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2016, Florian Hug. All rights reserved.			 *
 *																	 *
 * This file is part of the Jenkins Lockable Resources Plugin and is   *
 * published under the MIT license.									*
 *																	 *
 * See the "LICENSE.txt" file for more information.					*
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.jenkins.plugins.lockableresources.queue;

import java.io.Serializable;

import org.jenkinsci.plugins.workflow.steps.StepContext;

/* 
 * This class is used to queue pipeline contexts 
 * which shall be executed once the necessary
 * resources are free'd.
 */
public class QueuedContextStruct implements Serializable {

	/*
	 * Reference to the pipeline step context.
	 */
	private StepContext context;
	
	/*
	 * Reference to the resources required by the step context.
	 */
	private LockableResourcesStruct lockableResourcesStruct;
	
	/*
	 * Description of the required resources used within logging messages.
	 */
	private String resourceDescription;

	/*
	 * Priority this context should have in the queue. Lowest priority being 0.
	 */
	private int queuePriority = 0;

	/* 
	 * Name of the environment variable holding the resource name
	 */
	private String resourceVariableName;

	/*
	 * Constructor for the QueuedContextStruct class.
	 */
	public QueuedContextStruct(StepContext context,
				   LockableResourcesStruct lockableResourcesStruct,
				   String resourceDescription,
				   String resourceVariableName,
				   int queuePriority) {
		this.context = context;
		this.lockableResourcesStruct = lockableResourcesStruct;
		this.resourceDescription = resourceDescription;
		this.resourceVariableName = resourceVariableName;
		this.queuePriority = queuePriority;
	}

	/*
	 * Gets the pipeline step context.
	 */
	public StepContext getContext() {
		return this.context;
	}
	
	/*
	 * Gets the required resources.
	 */
	public LockableResourcesStruct getResources() {
		return this.lockableResourcesStruct;
	}

	/*
	 * Gets the resource description for logging messages.
	 */
	public String getResourceDescription() {
		return this.resourceDescription;
	}

	public int getQueuePriority() {
		return this.queuePriority;
	}

	public String getResourceVariableName() {
		return resourceVariableName;
	}

	private static final long serialVersionUID = 1L;
}
