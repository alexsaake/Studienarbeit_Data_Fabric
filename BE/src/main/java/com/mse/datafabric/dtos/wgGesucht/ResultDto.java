package com.mse.datafabric.dtos.wgGesucht;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ResultDto {

    private List<WgGesuchtDto> result = new ArrayList<>();

    private LocalDateTime refreshDate;

    private int pageSize;

    private int page;

    private int size;
}
