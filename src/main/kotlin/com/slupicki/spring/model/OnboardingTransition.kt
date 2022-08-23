package com.slupicki.spring.model

import com.slupicki.spring.model.OnboardingEvent.*
import com.slupicki.spring.model.OnboardingState.*

enum class OnboardingTransition(
    val sourceState: OnboardingState,
    val targetState: OnboardingState,
    val event: OnboardingEvent) {

    T_01(ONBOARDING_INIT, FINSTAR, SUCCESS),
    T_02(FINSTAR, SMARTTRADE, SUCCESS),
    T_03(SMARTTRADE, ATLAS, SUCCESS),
    T_04(ATLAS, ONBOARDING_ENDS, SUCCESS),

    T_05(FINSTAR, FINSTAR_MANUAL, FAILURE),
    T_06(SMARTTRADE, SMARTTRADE_MANUAL, FAILURE),
    T_07(ATLAS, ATLAS_MANUAL, FAILURE),

    T_08(FINSTAR_MANUAL, FINSTAR, RETRY),
    T_09(SMARTTRADE_MANUAL, SMARTTRADE, RETRY),
    T_10(ATLAS_MANUAL, ATLAS, RETRY),

    T_11(FINSTAR_MANUAL, SMARTTRADE, SUCCESS),
    T_12(SMARTTRADE_MANUAL, ATLAS, SUCCESS),
    T_13(ATLAS_MANUAL, ONBOARDING_ENDS, SUCCESS);
    
}