package com.cloudsimgui.backend.schedulers;

//import org.cloudsimplus.schedulers.cloudlet.Cloudlet;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;

/**
 * Time-shared scheduler that runs at most {@link #MAX_RUNNING} cloudlets at once;
 * the rest wait until a running cloudlet finishes.
 */
public class LimitedConcurrencyCloudletScheduler extends CloudletSchedulerTimeShared {

    //public static final int MAX_RUNNING = 2;

    //@Override
    //protected boolean canExecuteCloudletInternal(CloudletExecution cle) {
    //    return getCloudletExecList().size() < MAX_RUNNING;
    //}
}
