package com.mse.datafabric.immobilien.dtos.wgSuche;

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
public class WgSucheResponseDto {

    public List<WgSucheDto> result = new ArrayList<>();

    public LocalDateTime refreshDate;

    public int pageSize;

    public int page;

    public int size;
}
