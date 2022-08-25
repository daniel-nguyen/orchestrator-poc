package com.slupicki.spring.model

enum class OnboardingState(val label: String) {
    ONBOARDING_INIT("Onboarding init"),
    FINSTAR("Finstar"),
    FINSTAR_MANUAL("Finstar manual"),
    SMARTTRADE("SmartTrade"),
    SMARTTRADE_MANUAL("SmartTrade manual"),
    ATLAS("Atlas"),
    ATLAS_MANUAL("Atlas manual"),
    ONBOARDING_ENDS("Onboarding ends")
}