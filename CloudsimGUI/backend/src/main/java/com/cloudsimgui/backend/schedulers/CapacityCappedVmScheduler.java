package com.cloudsimgui.backend.schedulers;

import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.vms.Vm;

import java.util.stream.Stream;

/**
 * Time-shared VM scheduler that refuses VMs once the host's allocated MIPS
 * would exceed {@link #MAX_UTILIZATION} of its total capacity.
 */
public class CapacityCappedVmScheduler extends VmSchedulerTimeShared {

    public static final double MAX_UTILIZATION = 0.8;

    @Override
    protected boolean isSuitableForVmInternal(Vm vm, MipsShare requestedMips) {
        double cap = getHost().getTotalMipsCapacity() * MAX_UTILIZATION;
        double allocatedMips = Stream.concat(
                        getHost().getVmList().stream(),
                        getHost().getVmsMigratingIn().stream())
                .filter(existingVm -> !existingVm.equals(vm))
                .mapToDouble(this::getTotalAllocatedMipsForVm)
                .sum();
        return allocatedMips + requestedMips.totalMips() <= cap
                && super.isSuitableForVmInternal(vm, requestedMips);
    }
}
