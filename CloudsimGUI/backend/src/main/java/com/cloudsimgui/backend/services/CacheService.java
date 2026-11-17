package com.cloudsimgui.backend.services;

import com.cloudsimgui.backend.dto.ResultDto;
import com.cloudsimgui.backend.enums.Statuses;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    Map<String, Statuses> StatusById = new ConcurrentHashMap<>();
    Map<String, ResultDto> ResultById = new ConcurrentHashMap<>();

    public void AddStatus(String Id, Statuses status){
        this.StatusById.put(Id, status);
    }

    public Statuses GetStatus(String Id){
        return this.StatusById.getOrDefault(Id,Statuses.Not_Found);
    }

    public void AddResult(String Id, ResultDto result){
        this.ResultById.put(Id,result);
    }

    public ResultDto GetResult(String Id){
        return this.ResultById.getOrDefault(Id, null);
    }


}
