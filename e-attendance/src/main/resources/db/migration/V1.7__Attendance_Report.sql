drop function if exists attendance_report_funcs(_office_code character varying, date_from date, date_to date);
CREATE OR REPLACE FUNCTION public._attendance_report_funcs(_office_code character varying, date_from date, date_to date)
 RETURNS TABLE("pisCode" character varying,"employeeCode" character varying, "fullName" character varying, "employeePosition" character varying, "totalUninformedLeave" bigint, "totalLeave" bigint, "totalKaaj" bigint, "totalLate" bigint, "totalLeaveEarlier" character varying)
 LANGUAGE plpgsql
AS $function$
	declare
allowed_limit		bigint;
		maximum_late_checkin	time;
		maximum_early_checkout  time;
begin
select 	otc.allowed_limit,
          otc.maximum_late_checkin,
          otc.maximum_early_checkout
into allowed_limit,maximum_late_checkin,maximum_early_checkout
from office_time_config otc where otc.office_code =_office_code ;

return query
select e.pis_code::varchar piscode ,e.employee_code:: varchar employeeCode,
        concat(e.first_name_np,' ',e.middle_name_np,' ',e.last_name_np)::varchar full_name
		,p.name_np::varchar employee_position
		--,tt.total_late_checkin_exist
		--,ttt.total_early_checkout_exist
		,tttt.total_uninformed_leave::bigint
		,ttttt.total_leave::bigint
		,tttttt.total_kaaj::bigint

		,(case when tt.total_late_checkin_exist > allowed_limit then
                                                                                                                                                                                                        tt.total_late_checkin_exist-allowed_limit
               else 0 end)::bigint as total_late

		 ,(case when ttt.total_early_checkout_exist <= 0
                                              then 0
                when ttt.total_early_checkout_exist > allowed_limit
                                              then ttt.total_early_checkout_exist-allowed_limit
                else 0
    end)::varchar as total_leave_earlier
from
    employee e
        inner join "position" p on(p.code=e.position_code)
        inner join functional_designation fd on (e.designation_code=fd.code)

        left outer join (select count(ea.pis_code) total_late_checkin_exist, ea.pis_code
                         from employee_attendance ea
                         where ea.office_code = office_code
                           and ea.checkin - ea.shift_checkin > maximum_late_checkin
                           and ea.date_en between date_from and date_to
                         group by ea.pis_code
    ) tt on e.pis_code=tt.pis_code
        left outer join (select count(ea.pis_code)  total_early_checkout_exist,ea.pis_code
                         from employee_attendance ea
                         where ea.office_code = office_code
                           and case
                                   when(ea.checkout is not null) then
                                       (ea.shift_checkout - ea.checkout  > maximum_early_checkout)
                                   else (ea.shift_checkout -ea.checkin >maximum_early_checkout)
                             end
                           and ea.date_en between date_from and date_to
                         group by ea.pis_code
    )ttt on ttt.pis_code=e.pis_code
        left outer join (select count(ea.pis_code) total_uninformed_leave ,ea.pis_code
                         from employee_attendance ea
                         where ea.attendance_status ='UNINOFRMED_LEAVE_ABSENT'
                           and ea.date_en between date_from and date_to
                         group by ea.pis_code
    )tttt on tttt.pis_code=e.pis_code
        left outer join (select count(ea.pis_code)  total_leave ,ea.pis_code
                         from employee_attendance ea
                         where ea.attendance_status ='LEAVE'
                           and ea.date_en between date_from and date_to
                         group by ea.pis_code) ttttt on ttttt.pis_CODE=e.pis_code
        left outer join (select count(ea.pis_code) total_kaaj ,ea.pis_code
                         from employee_attendance ea
                         where ea.attendance_status ='LEAVE'
                           and ea.date_en between date_from and date_to
                         group by ea.pis_code) tttttt on tttttt.pis_code=e.pis_code

where e.office_code =_office_code and fd.designation_type='NORMAL_DESIGNATION'
	order by p.order_no ,e.cur_office_join_dt_en;
END;
$function$
;