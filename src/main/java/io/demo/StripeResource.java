package io.demo;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.StripeObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * REST controller to pay with Stripe.
 */
@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class StripeResource {

    private final Logger log = LoggerFactory.getLogger(StripeResource.class);

    @PostMapping("/webhook")
    public Mono<ResponseEntity<Void>> webhookAfterPayment(@RequestBody final Event event, ServerWebExchange request) {
        log.debug("REST request to Post hook from Stripe : Event {}", event);


        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject                stripeObject           = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
            log.error("Deserialization failed, probably due to an API version mismatch");
        }

        // Handle the event
        switch (event.getType()) {
        case "payment_intent.succeeded":
            //TODO stripeObject is NULL
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            log.debug("Payment for id={} and amount={} succeeded.", paymentIntent.getId(), paymentIntent.getAmount());
            // Then define and call a method to handle the successful payment intent.
            // handlePaymentIntentSucceeded(paymentIntent);
            break;
        case "payment_method.attached":
            PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
            // Then define and call a method to handle the successful attachment of a PaymentMethod.
            // handlePaymentMethodAttached(paymentMethod);
            break;
        default:
            log.error("Unhandled event type: {} ", event.getType());
            break;
        }

        return Mono.just(ResponseEntity.ok().build());
    }

}
