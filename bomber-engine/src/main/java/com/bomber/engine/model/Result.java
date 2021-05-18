package com.bomber.engine.model;

import java.util.Date;

import org.springframework.lang.NonNull;

import com.bomber.engine.rpc.BombardierResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果
 *
 * @author MingMing Zhao
 */
@Getter
@AllArgsConstructor
public class Result {

    @NonNull
    private final Date startTime;

    @NonNull
    private final Date endTime;

    @NonNull
    private final BombardierResponse response;
}
