insert into state_machine_type(id, name) VALUES (1, 'ONBOARDING');

insert into state(id, final, initial, name) VALUES (1, false, true, 'onboarding init');
insert into state(id, final, initial, name) VALUES (2, false, false, 'Finstar');
insert into state(id, final, initial, name) VALUES (3, false, false, 'Finstar Manual');
insert into state(id, final, initial, name) VALUES (4, false, false, 'SmartTrade');
insert into state(id, final, initial, name) VALUES (5, false, false, 'SmartTrade Manual');
insert into state(id, final, initial, name) VALUES (6, false, false, 'Atlas');
insert into state(id, final, initial, name) VALUES (7, false, false, 'Atlas Manual');
insert into state(id, final, initial, name) VALUES (8, true, false, 'Onboarding ends');

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(1, 1, 'FINSTAR', 'SUCCESS', 1, 2);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(2, 1, 'SMART_TRADE', 'SUCCESS', 2, 4);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(3, 1, 'ATLAS', 'SUCCESS', 4, 6);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(4, 1, 'SUCCESFUL_ONBOARDING', 'SUCCESS', 6, 8);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(5, 1, 'FINSTAR_MANUAL', 'FAILURE', 2, 3);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(6, 1, 'SMART_TRADE', 'SUCCESS', 3, 4);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(7, 1, 'SMART_TRADE_MANUAL', 'FAILURE', 4, 5);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(8, 1, 'ATLAS', 'SUCCESS', 5, 6);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(9, 1, 'ATLAS_MANUAL', 'FAILURE', 6, 7);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(10, 1, 'SUCCESFUL_ONBOARDING', 'SUCCESS', 7, 8);
