package com.erickbarbosa.rupturainfinita;

final class ForgeException extends RuntimeException {
    enum Reason { INSUFFICIENT_ITEMS, INVENTORY_FULL }
    final Reason reason;

    ForgeException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }
}
