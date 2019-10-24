package it.polito.ai.services;

import it.polito.ai.models.*;
import it.polito.ai.repositories.PurchaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private static final double COST_PER_POSITION = 1;

    @Autowired
    private PurchaseRepo purchaseRepo;

    @Autowired
    private PositionService positionService;

    @Autowired
    private AccountService accountService;

    public PurchaseResponse requestPurchase(
            List<PositionEntry> positions,
            Long from,
            Long to
    ) {
        List<PositionEntry> points = positionService.getPositionsWithinPolygon(positions, from, to);

        PurchaseResponse response = new PurchaseResponse();
        response.setAmount(points.size() * COST_PER_POSITION);
        response.setPositionsNumber(points.size());
        response.setPositions(new ArrayList<>());

        return response;
    }

    @Transactional
    public Purchase confirmPurchase(
            String userId,
            List<PositionEntry> positions,
            Long from,
            Long to
    ) {
        List<PositionEntry> points = positionService.getPositionsWithinPolygon(positions, from, to);

        Purchase purchase = new Purchase();
        purchase.setAmount(points.size() * COST_PER_POSITION);
        purchase.setPositionsNumber(points.size());
        purchase.setPositions(points);
        purchase.setUserId(userId);

        for (PositionEntry p: points) {
            Account account = accountService.findAccountById(p.getUserId());

            double oldWallet = account.getWallet();
            account.setWallet(oldWallet + COST_PER_POSITION);

            accountService.saveAccount(account);
        }

        return purchaseRepo.save(purchase);
    }

    public List<Purchase> getPurchases(String userId) {
        return purchaseRepo.findAllByUserIdEquals(userId);
    }

}
