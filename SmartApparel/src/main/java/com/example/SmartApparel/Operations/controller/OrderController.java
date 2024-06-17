package com.example.SmartApparel.Operations.controller;

import com.example.SmartApparel.Operations.dto.OrderDTO;
import com.example.SmartApparel.Operations.dto.ResponseDTO;
import com.example.SmartApparel.Operations.entity.Order;
import com.example.SmartApparel.Operations.service.OrderService;
import com.example.SmartApparel.Operations.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    // Autowired ResponseDTO and OrderService
    @Autowired
    private ResponseDTO responseDTO;
    @Autowired
    private OrderService orderService;

    // Save order details
    @PostMapping(value = "/saveOrder")
    public ResponseEntity saveOrder(@RequestBody OrderDTO orderDTO){
        try {
            String response = orderService.saveOrder(orderDTO);
            // Handle different responses from the service
            if (response.equals("00")){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Saved Successfully.");
                responseDTO.setContent(orderDTO);
                return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
            } else if (response.equals("06")) {
                responseDTO.setCode(VarList.RSP_DUPLICATED);
                responseDTO.setMessage("Already Registered.");
                responseDTO.setContent(orderDTO);
                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
            }else {
                responseDTO.setCode(VarList.RSP_FAIL);
                responseDTO.setMessage("Error");
                responseDTO.setContent(null);
                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update order details
    @PutMapping (value = "/updateOrder")
    public ResponseEntity updateOrder(@RequestBody OrderDTO orderDTO){
        try {
            String response = orderService.updateOrder(orderDTO);
            // Handle different responses from the service
            if (response.equals("00")){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Saved Successfully.");
                responseDTO.setContent(orderDTO);
                return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
            } else if (response.equals("01")) {
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("Not a Registered Employee");
                responseDTO.setContent(orderDTO);
                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
            }else {
                responseDTO.setCode(VarList.RSP_FAIL);
                responseDTO.setMessage("Error");
                responseDTO.setContent(null);
                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // View all orders
    @GetMapping (value = "/viewOrder")
    public ResponseEntity viewOrder(){
        try {
            // Retrieve list of orders
            List<OrderDTO> orderDTOList = orderService.viewOrder();
            responseDTO.setCode(VarList.RSP_SUCCESS);
            responseDTO.setMessage("Saved Successfully.");
            responseDTO.setContent(orderDTOList);
            return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
        }catch (Exception e){
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/viewOrder/{orderId}")
    public ResponseEntity<ResponseDTO> viewCustomerById(@PathVariable("orderId") Integer OrderId) {
        try {
            OrderDTO orderDTO = orderService.viewOrderById(OrderId);
            responseDTO.setCode(VarList.RSP_SUCCESS);
            responseDTO.setMessage("Customer retrieved successfully.");
            responseDTO.setContent(orderDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a order
    @DeleteMapping (value = "/deleteOrder/{OrderId}")
    public ResponseEntity deleteOrder(@PathVariable int OrderId){
        try {
            String response = orderService.deleteOrder(OrderId);
            // Handle different responses from the service
            if (response.equals("00")){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successful.");
                responseDTO.setContent(null);
                return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
            }else {
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("No order available.");
                responseDTO.setContent(null);
                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkShipped/{OrderId}")
    public ResponseEntity<String> checkOrderShipped(@PathVariable Integer OrderId) throws Exception {
        if (orderService.isOrderShipped(OrderId)) {
//            return ResponseEntity.ok("Shipped");
            responseDTO.setCode(VarList.RSP_SUCCESS);
            responseDTO.setMessage("Successful.");
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
        } else {
//            return ResponseEntity.status(404).body("Order not found or not shipped");
            responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
            responseDTO.setMessage("Order not found or not shipped");
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/generateBill/{OrderId}")
    public ResponseEntity<InputStreamResource> generateBill(@PathVariable Integer OrderId) throws Exception {
        if (!orderService.isOrderShipped(OrderId)) {
            return ResponseEntity.status(404).body(null);
        }
        // Simulate PDF generation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(("Order Bill for Order ID: " + OrderId).getBytes());
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=order_bill.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }


    // Search for an order by ID
//    @GetMapping (value = "/searchOrder/{OrderId}")
//    public ResponseEntity searchOrder(@PathVariable int OrderId){
//        try {
//            OrderDTO orderDTO = orderService.searchOrder(OrderId);
//            if (orderDTO != null){
//                responseDTO.setCode(VarList.RSP_SUCCESS);
//                responseDTO.setMessage("Successful.");
//                responseDTO.setContent(orderDTO);
//                return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);
//            }else {
//                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
//                responseDTO.setMessage("No such orderId.");
//                responseDTO.setContent(null);
//                return new ResponseEntity(responseDTO, HttpStatus.BAD_REQUEST);
//            }
//        }catch (Exception e){
//            responseDTO.setCode(VarList.RSP_ERROR);
//            responseDTO.setMessage(e.getMessage());
//            responseDTO.setContent(null);
//            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    // To get Total sum of Expenses
    @GetMapping("/completedOrderId")
    public ResponseEntity<ResponseDTO> getCompletedOrderIds() {

        try {
            List<Integer> orderCompleted = orderService.getCompletedOrderIds();

            responseDTO.setCode(VarList.RSP_SUCCESS);
            responseDTO.setMessage("Total completed order list successfully.");
            responseDTO.setContent(orderCompleted);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage("Failed to retrieve completed order list : " + e.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
