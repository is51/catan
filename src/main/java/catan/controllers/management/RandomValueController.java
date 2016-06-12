package catan.controllers.management;

import catan.domain.model.dashboard.Coordinates;
import catan.domain.model.dashboard.types.HexType;
import catan.domain.model.game.types.DevelopmentCard;
import catan.services.util.random.RandomValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/management/random")
public class RandomValueController {

    @Autowired
    private RandomValueProvider randomValueProvider;

    @RequestMapping(value = "set-next-private-code",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextPrivateCode(@RequestParam(value = "privateCode") String privateCode) {
        randomValueProvider.setNextPrivateCode(privateCode);
    }

    @RequestMapping(value = "reset-next-random-values",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void resetNextRandomValues() {
        randomValueProvider.resetNextRandomValues();
    }

    @RequestMapping(value = "set-next-move-order",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextMoveOrder(@RequestParam(value = "moveOrder") String moveOrder) {
        randomValueProvider.setNextMoveOrder(Integer.valueOf(moveOrder));
    }

    @RequestMapping(value = "set-next-hex-type",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextHexType(@RequestParam(value = "x") int x,
                               @RequestParam(value = "y") int y,
                               @RequestParam(value = "hexType") String hexType) {
        randomValueProvider.setNextHexType(new Coordinates(x, y), HexType.valueOf(hexType));
    }

    @RequestMapping(value = "set-next-hex-dice-number",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextHexDiceNumber(@RequestParam(value = "x") int x,
                                     @RequestParam(value = "y") int y,
                                     @RequestParam(value = "diceNumber") String diceNumber) {
        randomValueProvider.setNextHexDiceNumber(new Coordinates(x, y), diceNumber != null && !diceNumber.isEmpty() ? Integer.valueOf(diceNumber) : null);
    }

    @RequestMapping(value = "set-next-dice-number",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextDiceNumber(@RequestParam(value = "diceNumber") String diceNumber) {
        randomValueProvider.setNextDiceNumber(Integer.valueOf(diceNumber));
    }

    @RequestMapping(value = "set-next-development-card",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextDevelopmentCard(@RequestParam(value = "devCard") String devCard) {
        randomValueProvider.setNextDevelopmentCard(DevelopmentCard.valueOf(devCard));
    }

    @RequestMapping(value = "set-next-stolen-resource",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextStolenResource(@RequestParam(value = "resource") String resource) {
        randomValueProvider.setNextStolenResource(HexType.valueOf(resource));
    }

    @RequestMapping(value = "set-next-offer-id",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNextOfferId(@RequestParam(value = "offerId") String offerId) {
        randomValueProvider.setNextOfferId(Integer.valueOf(offerId));
    }
}
