package com.ab.ms.order.controller;

import com.ab.ms.order.dto.ErrorResponseDto;
import com.ab.ms.order.dto.InvalidDataErrorDto;
import com.ab.ms.order.dto.OrderDto;
import com.ab.ms.order.dto.SagaEventRequestDto;
import com.ab.ms.order.mapper.OrderMapper;
import com.ab.ms.order.saga.SagaOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for manual event injection for testing purpose
 */
@Tag(name = "Order saga orchestration", description = "Drive saga state transitions (in-process until messaging exists)")
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class SagaOrchestrationController {

    private final SagaOrchestrator sagaOrchestrator;

    public SagaOrchestrationController(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @Operation(summary = "Apply saga incoming event", description = "Validates transition, updates order status, dispatches next command when present (logged in-process).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transition applied"),
            @ApiResponse(responseCode = "400", description = "Invalid body or orderId mismatch", content = @Content(schema = @Schema(implementation = InvalidDataErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Illegal transition", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/orders/saga-events")
    public ResponseEntity<OrderDto> postSagaEvent(
            @Valid @RequestBody SagaEventRequestDto requestDto
    ) {
        var order = sagaOrchestrator.handleEvent(requestDto.getEvent());
        return ResponseEntity.ok(OrderMapper.toDto(order));
    }
}
