package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;

public interface TQFieldAssembler {
	Symbol toSymbol(TQSecID_F id);
	boolean toSecDisplayName(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecLotSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecTickSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecTickValue(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecInitialMargin(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecSettlementPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecLowerPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecUpperPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecOpenPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecHighPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecLowPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecClosePrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
	boolean toSecExpirationTime(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder);
}
