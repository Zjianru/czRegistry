package com.cz.registry.common;

import com.cz.registry.exception.RegistryException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * response info
 *
 * @author Zjianru
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Response <T> implements Serializable {
    boolean status;
    T data;
    RegistryException exception;
}
