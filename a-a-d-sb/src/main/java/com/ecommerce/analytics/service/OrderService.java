package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.CheckoutRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.*;
import com.ecommerce.analytics.model.enums.OrderStatus;
import com.ecommerce.analytics.model.enums.PaymentStatus;
import com.ecommerce.analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;
    private final CartRepository cartRepository;
    private final UserAddressRepository userAddressRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public OrderResponse checkout(String userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));
        if (cart.getItems().isEmpty()) throw new IllegalStateException("Cart is empty");

        UserAddress address = userAddressRepository.findByAddressIdAndUserUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Coupon coupon = null;
        if (request.getCouponCode() != null) {
            coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode()).orElse(null);
        }

        Store store = cart.getItems().get(0).getProduct().getStore();
        BigDecimal totalAmount = cart.getItems().stream()
                .map(i -> i.getProduct().getBasePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (coupon != null) {
            BigDecimal discount = totalAmount.multiply(coupon.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
            totalAmount = totalAmount.subtract(discount);
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .store(store)
                .coupon(coupon)
                .totalAmount(totalAmount)
                .status(OrderStatus.Pending)
                .fulfilment("Merchant")
                .build();
        orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPriceAtSale(cartItem.getProduct().getBasePrice())
                    .totalItemPrice(cartItem.getProduct().getBasePrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            orderItemRepository.save(orderItem);
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.Pending)
                .build();
        paymentRepository.save(payment);

        cart.getItems().clear();
        cartRepository.save(cart);

        return getOrderById(userId, order.getOrderId());
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(String userId, Pageable pageable) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId, pageable)
                .map(o -> buildOrderResponse(o));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userId, String orderId) {
        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return buildOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(String userId, String orderId) {
        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        if (order.getStatus() != OrderStatus.Pending)
            throw new IllegalStateException("Only pending orders can be cancelled");
        order.setStatus(OrderStatus.Cancelled);
        orderRepository.save(order);
        return buildOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public String exportOrdersCsv(String userId, LocalDateTime from, LocalDateTime to) {
        List<Order> orders = orderRepository.findByUserForExport(userId, from, to);
        StringBuilder csv = new StringBuilder();
        csv.append("Order ID,Order Number,Store,Total Amount,Status,Payment Method,Order Date\n");
        for (Order o : orders) {
            String paymentMethod = paymentRepository.findByOrderOrderId(o.getOrderId())
                    .map(p -> p.getPaymentMethod().name()).orElse("");
            csv.append(String.join(",",
                    o.getOrderId(),
                    o.getOrderNumber() != null ? o.getOrderNumber() : "",
                    o.getStore().getStoreName(),
                    o.getTotalAmount().toPlainString(),
                    o.getStatus().name(),
                    paymentMethod,
                    o.getOrderDate() != null ? o.getOrderDate().toString() : ""
            )).append("\n");
        }
        return csv.toString();
    }

    public OrderResponse buildOrderResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderOrderId(order.getOrderId())
                .stream().map(i -> OrderItemResponse.builder()
                        .orderItemId(i.getOrderItemId())
                        .productId(i.getProduct().getProductId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPriceAtSale(i.getUnitPriceAtSale())
                        .totalItemPrice(i.getTotalItemPrice())
                        .build()).collect(Collectors.toList());

        ShipmentResponse shipmentResponse = shipmentRepository.findByOrderOrderId(order.getOrderId())
                .map(s -> ShipmentResponse.builder()
                        .shipmentId(s.getShipmentId())
                        .orderId(s.getOrder().getOrderId())
                        .trackingNumber(s.getTrackingNumber())
                        .carrierName(s.getCarrierName())
                        .modeOfShipment(s.getModeOfShipment())
                        .shippingStatus(s.getShippingStatus())
                        .estimatedDelivery(s.getEstimatedDelivery())
                        .build()).orElse(null);

        PaymentResponse paymentResponse = paymentRepository.findByOrderOrderId(order.getOrderId())
                .map(p -> PaymentResponse.builder()
                        .paymentId(p.getPaymentId())
                        .paymentMethod(p.getPaymentMethod().name())
                        .paymentStatus(p.getPaymentStatus().name())
                        .transactionId(p.getTransactionId())
                        .paidAt(p.getPaidAt())
                        .build()).orElse(null);

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .storeId(order.getStore().getStoreId())
                .storeName(order.getStore().getStoreName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .fulfilment(order.getFulfilment())
                .orderDate(order.getOrderDate())
                .items(items)
                .shipment(shipmentResponse)
                .payment(paymentResponse)
                .build();
    }
}
