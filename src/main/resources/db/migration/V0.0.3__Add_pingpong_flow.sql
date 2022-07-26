insert into state_machine_type(id, name) VALUES (2, 'PING-PONG');

insert into state(id, final, initial, name) VALUES (10, false, true, 'New set: A serv');
insert into state(id, final, initial, name) VALUES (11, false, false, 'B receive');
insert into state(id, final, initial, name) VALUES (12, false, false, 'B failed to receive');
insert into state(id, final, initial, name) VALUES (13, false, false, 'A receive');
insert into state(id, final, initial, name) VALUES (14, false, false, 'A failed to receive');
insert into state(id, final, initial, name) VALUES (15, false, true, 'New set: B serv');

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(20, 2, 'B_RECEIVE', 'SUCCESS', 10, 11);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(21, 2, 'B_FAILED_RECEIVE', 'FAILURE', 10, 12);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(22, 2, 'A_RECEIVE', 'SUCCESS', 11, 13);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(24, 2, 'A_FAILED_TO_RECEIVE', 'FAILURE', 11, 14);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(25, 2, 'A_SERV', 'SUCCESS', 12, 10);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(26, 2, 'B_RECEIVE', 'SUCCESS', 13, 11);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(27, 2, 'B_FAILED_RECEIVE', 'FAILURE', 13, 12);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(28, 2, 'B_SERV', 'SUCCESS', 14, 15);

insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(29, 2, 'A_RECEIVE', 'SUCCESS', 15, 13);
insert into transition(id, state_machine_type_id, action, event, from_state_id, to_state_id)
VALUES(30, 2, 'A_FAILED_TO_RECEIVE', 'FAILURE', 15, 14);
