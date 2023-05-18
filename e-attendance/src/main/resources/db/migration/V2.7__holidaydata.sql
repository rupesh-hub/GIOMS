CREATE OR REPLACE FUNCTION public.holiday_return_data(v_date_np character varying)
 RETURNS boolean
 LANGUAGE plpgsql
AS $function$
-- Declare Branch Record and GL Record
DECLARE
additionaldata RECORD;
    insertingLoopforemployee RECORD;
    lid RECORD;
    department_idmasterid integer;
    val_leave_detail_id int;
    val_leave_policy_id int;
    val_year varchar;
   val_holiday boolean;
begin
FOR lid in (select  ea.pis_code as pis_code,
 					e.employee_service_status_code as service_code,
                     ea.attendance_status
                from employee_attendance ea inner join employee e on e.pis_code =ea.pis_code
                where ea.date_np =v_date_np::varchar
                and ea.attendance_status in ('LEAVE')
            )
 loop

select lrd.id as leave_detail_id,
       lrd.leave_policy_id as leave_policy_id,
       lrd.year  as year,lp.count_public_holiday as holiday_count
into val_leave_detail_id, val_leave_policy_id,val_year,val_holiday
from leave_request lr inner join leave_request_detail lrd
on lr.id=lrd.leave_request_id
    inner join leave_policy lp on lrd.leave_policy_id =lp.id
where v_date_np::varchar between lrd.from_date_np and lrd.to_date_np
  and case
    when lr.applied_for_others= true then
    lrd.pis_code =lid.pis_code
    else
    lr.emp_pis_code =lid.pis_code
end;


if val_leave_policy_id != 25 then
update leave_request_detail
set actual_leave_days =actual_leave_days-1
where id=val_leave_detail_id;

if val_holiday =false then

  		if lid.service_code='04' then

update remaining_leave
set monthly_leave_taken =monthly_leave_taken::int  - 1,
        	remaining_leave =remaining_leave::int - 1
where pis_code =lid.pis_code
  and leave_policy_id =val_leave_policy_id
  and "year" =val_year;

else
update remaining_leave
set accumulated_leave_fy = case  when leave_policy_id =22 then
                                     accumulated_leave_fy::int + 1 else accumulated_leave_fy
end ,
	            leave_taken=case when leave_policy_id in (27,28) then
	                leave_taken::int - 1 else leave_taken
end,
	            leave_taken_fy= case when leave_policy_id not in (22,27,28)
	            then   leave_taken_fy::int  - 1 else
	            leave_taken_fy end,
	        	remaining_leave =remaining_leave::int - 1
	       		where pis_code =lid.pis_code
	        	and leave_policy_id =val_leave_policy_id
	        	and "year" =val_year;
end if;
end if;
end if;
update employee_attendance
set attendance_status ='PUBLIC_HOLIDAY',is_holiday =true
where date_np::varchar =v_date_np;

END LOOP;
RETURN TRUE;
END;
$function$
;
