/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.axis.deployment.scheduler;

import org.apache.axis.deployment.DeploymentConstants;
import org.apache.axis.deployment.DeploymentEngine;
import org.apache.axis.deployment.listener.RepositoryListener;
import org.apache.axis.deployment.listener.RepositoryListenerImpl;

import java.util.TimerTask;

public class SchedulerTask implements Runnable, DeploymentConstants {
    final Object lock = new Object();

    private RepositoryListener wsListener;

    int state = VIRGIN;
    static final int VIRGIN = 0;
    static final int SCHEDULED = 1;
    static final int CANCELLED = 2;

    TimerTask timerTask;

    /**
     * Creates a new scheduler task.
     */

    public SchedulerTask(DeploymentEngine deploy_engine, String folderName) {
        //     String filename = FOLDE_NAME; //"D:/Axis 2.0/projects/Deployement/test-data" ;
        //  private  FilesLoader filesLoader = new FilesLoader(filename);
        wsListener = new RepositoryListenerImpl(folderName, deploy_engine);
    }

    /**
     * The action to be performed by this scheduler task.
     */

    public void run() {
        soundAlarm();
    }

    private void soundAlarm() {
        ((RepositoryListenerImpl) wsListener).startListent();
        //filesLoader.searchFolder();
    }

    /**
     * Cancels this scheduler task.
     * <p/>
     * This method may be called repeatedly; the second and subsequent calls have no effect.
     *
     * @return true if this task was already scheduled to run
     */

    public boolean cancel() {
        synchronized (lock) {
            if (timerTask != null) {
                timerTask.cancel();
            }
            boolean result = (state == SCHEDULED);
            state = CANCELLED;
            return result;
        }
    }

    /**
     * Returns the <i>scheduled</i> execution time of the most recent actual execution of this task.
     * (If this method is invoked while task execution is in progress, the return value is the
     * scheduled execution time of the ongoing task execution.)
     *
     * @return the time at which the most recent execution of this task was scheduled to occur,
     *         in the format returned by <code>Date.getTime()</code>. The return value is
     *         undefined if the task has yet to commence its first execution.
     */

    public long scheduledExecutionTime() {
        synchronized (lock) {
            return timerTask == null ? 0 : timerTask.scheduledExecutionTime();
        }
    }

}
