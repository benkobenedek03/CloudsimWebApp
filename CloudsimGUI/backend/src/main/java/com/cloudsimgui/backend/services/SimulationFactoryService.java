package com.cloudsimgui.backend.services;

import com.cloudsimgui.backend.dto.*;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.security.InvalidParameterException;


@Service
public class SimulationFactoryService {

    public SimulationContext GetSimulation(SimulationRequestDto request){
        if (request.getSimpleRequest() != null) { return GetSimpleSimulation(request.getSimpleRequest()); }
        if (request.getDetailedRequest() != null) { return GetDetailedSimulation(request.getDetailedRequest()); }
        throw new InvalidParameterException("The given request is not valid.");
    }

    private SimulationContext GetSimpleSimulation(SimpleRequestDTO request){
        CloudSimPlus simulation = new CloudSimPlus();
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);

        for (int d = 0; d < request.getDatacenterCount(); d++) {
            List<Host> hostList = new ArrayList<>();
            for (int h = 0; h < request.getHostCount(); h++) {
                List<Pe> peList = new ArrayList<>();
                for (int p = 0; p < request.getHostPes(); p++) {
                    peList.add(new PeSimple(request.getHostMips()));
                }
                Host host = new HostSimple(request.getHostRam(), 10000, 1000000, peList);
                host.setVmScheduler("SPACE_SHARED".equalsIgnoreCase(request.getSchedulingPolicy())
                        ? new VmSchedulerSpaceShared() : new VmSchedulerTimeShared());
                hostList.add(host);
            }
            new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple()).getCharacteristics()
                    .setCostPerSecond(3.00)
                    .setCostPerMem(0.05);
        }

        // 2. VM creation
        List<Vm> vmList = new ArrayList<>();
        for (int v = 0; v < request.getVmCount(); v++) {
            Vm vm = new VmSimple(v, request.getVmMips(), request.getVmPes())
                    .setRam(request.getVmRam())
                    .setBw(1000)
                    .setSize(10000);
            vm.setCloudletScheduler(request.getSchedulingPolicy().equalsIgnoreCase("SPACE_SHARED")
                    ? new CloudletSchedulerSpaceShared() : new CloudletSchedulerTimeShared());
            vmList.add(vm);
        }
        broker.submitVmList(vmList);

        // 3. Cloudlet creation
        List<Cloudlet> cloudletList = new ArrayList<>();
        UtilizationModelFull fullModel = new UtilizationModelFull();
        for (int c = 0; c < request.getCloudletCount(); c++) {
            Cloudlet cl = new CloudletSimple(c, request.getCloudletLength(), request.getCloudletPes())
                    .setFileSize(300)
                    .setOutputSize(300)
                    .setUtilizationModel(fullModel);
            cloudletList.add(cl);
        }
        broker.submitCloudletList(cloudletList);

        return new SimulationContext(simulation,broker,cloudletList.size());
    }

    private SimulationContext GetDetailedSimulation(DetailedRequestDto request){

        CloudSimPlus simulation=new CloudSimPlus();
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);

        // 1. Detailed Datacenter creation
        for (DatacenterDto dcDto : request.getDatacenters()) {
            List<Host> hostList = new ArrayList<>();
            for (HostDto hDto : dcDto.getHosts()) {
                List<Pe> peList = new ArrayList<>();
                for (int i = 0; i < hDto.getPes(); i++) {
                    peList.add(new PeSimple(hDto.getMips()));
                }

                Host host = new HostSimple(hDto.getRam(), hDto.getBw(), hDto.getStorage(), peList);
                host.setVmScheduler(resolveVmScheduler(hDto));
                hostList.add(host);
            }

            Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
            dc.getCharacteristics()
                    .setCostPerSecond(dcDto.getCostPerSec())
                    .setCostPerMem(dcDto.getCostPerMem())
                    .setCostPerStorage(dcDto.getCostPerStorage())
                    .setCostPerBw(dcDto.getCostPerBw());
        }

        // 2. Detailed VM creation
        List<Vm> vmList = new ArrayList<>();
        for (VmDto vmDto : request.getVms()) {

            Vm vm = new VmSimple(vmDto.getId(), vmDto.getMips(), vmDto.getPes());
            vm.setRam(vmDto.getRam())
                    .setBw(vmDto.getBw())
                    .setSize(vmDto.getSize());

            vm.setCloudletScheduler(resolveCloudletScheduler(vmDto));
            vmList.add(vm);
        }
        broker.submitVmList(vmList);

        // 3. Detailed cloudlet creation
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (CloudletDto cDto : request.getCloudlets()) {
            UtilizationModelFull fullModel = new UtilizationModelFull();
            Cloudlet cl = new CloudletSimple(cDto.getId(),cDto.getLength(), cDto.getPes())
                    .setFileSize(cDto.getFileSize())
                    .setOutputSize(cDto.getOutputSize())
                    .setUtilizationModelCpu(fullModel);
            cloudletList.add(cl);
        }
        broker.submitCloudletList(cloudletList);
        return new SimulationContext(simulation,broker,cloudletList.size());
    }

    private VmScheduler resolveVmScheduler(HostDto hDto) {
        if ("CUSTOM".equalsIgnoreCase(hDto.getVmScheduler()) && hDto.getCustomSchedulerClass() != null) {
            try {
                Class<?> clazz = Class.forName(hDto.getCustomSchedulerClass());
                return (VmScheduler) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not load custom VmScheduler class: "
                        + hDto.getCustomSchedulerClass(), e);
            }
        }
        if ("TimeShared".equalsIgnoreCase(hDto.getVmScheduler())) {
            return new VmSchedulerTimeShared();
        }
        return new VmSchedulerSpaceShared();
    }

    private CloudletScheduler resolveCloudletScheduler(VmDto vmDto) {
        if ("CUSTOM".equalsIgnoreCase(vmDto.getCloudletScheduler()) && vmDto.getCustomSchedulerClass() != null) {
            try {
                Class<?> clazz = Class.forName(vmDto.getCustomSchedulerClass());
                return (CloudletScheduler) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not load custom CloudletScheduler class: "
                        + vmDto.getCustomSchedulerClass(), e);
            }
        }
        if ("TimeShared".equalsIgnoreCase(vmDto.getCloudletScheduler())) {
            return new CloudletSchedulerTimeShared();
        }
        return new CloudletSchedulerSpaceShared();
    }

}
