package com.key.jorigin.rocketmq;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserSubscriptionProd {

    //key:userId val: prodId set
    private static Map<Long, Set<Long>> userSubProdCache = new
            ConcurrentHashMap<Long, Set<Long>>();
    //key : prodId, val : subNums
    private Map<Long, Long> prodSubStatCache = new ConcurrentHashMap<Long, Long>();
    private Object lock = new Object();

    public boolean userSubProd(Long userId, Long prodId) {
        Set<Long> prodIdSet = userSubProdCache.get(userId);
        if (CollectionUtils.isEmpty(prodIdSet)) {
            synchronized (lock) {
                if (CollectionUtils.isEmpty(prodIdSet)) {
                    prodIdSet = new HashSet<Long>();
                }
            }
            userSubProdCache.put(userId, prodIdSet);
        }
        if (!prodIdSet.contains(prodId)) {
            prodIdSet.add(prodId);
            incr(prodId);
        }
        return true;
    }

    public Long getAllProdSubNums() {

        Long count = 0l;
        for (Long prodId : prodSubStatCache.keySet()) {

            count += prodSubStatCache.get(prodId);
        }
        return count;

    }

    public Long getSubNumsByProdId(Long prodId) {
        Long num = prodSubStatCache.get(prodId);
        return num == null ? 0 : num;
    }

    private boolean incr(Long prodId) {

        Long subNums = prodSubStatCache.get(prodId);
        if (subNums == null) {
            prodSubStatCache.put(prodId, 1l);
        } else {
            prodSubStatCache.put(prodId, subNums + 1l);
        }
        return true;
    }
}























