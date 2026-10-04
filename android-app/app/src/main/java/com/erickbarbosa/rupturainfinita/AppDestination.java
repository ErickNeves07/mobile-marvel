package com.erickbarbosa.rupturainfinita;

/** Top-level destinations in the order approved for the app shell. */
enum AppDestination {
    NEXUS(R.string.nav_nexus, R.string.nexus_description, R.drawable.ic_nav_nexus),
    CAMPAIGNS(R.string.nav_campaigns, R.string.campaigns_description, R.drawable.ic_nav_campaigns),
    FORGE(R.string.nav_forge, R.string.forge_description, R.drawable.ic_nav_forge),
    COLLECTION(R.string.nav_collection, R.string.collection_description, R.drawable.ic_nav_collection),
    DEADPOOL(R.string.nav_deadpool, R.string.deadpool_description, R.drawable.ic_nav_deadpool);

    final int labelRes;
    final int descriptionRes;
    final int iconRes;

    AppDestination(int labelRes, int descriptionRes, int iconRes) {
        this.labelRes = labelRes;
        this.descriptionRes = descriptionRes;
        this.iconRes = iconRes;
    }

    static AppDestination initial() {
        return FORGE;
    }

    static AppDestination[] nexusShortcuts() {
        return new AppDestination[] { CAMPAIGNS, FORGE, COLLECTION, DEADPOOL };
    }
}
