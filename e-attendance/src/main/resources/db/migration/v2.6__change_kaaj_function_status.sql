
CREATE OR REPLACE FUNCTION public.attendance_annual_report(piscode character varying, filder_date int, filter_month int)
 RETURNS TABLE(checkin time without time zone, checkout time without time zone, "dateEn" date, "dateNp" character varying, "pisCode" character varying, "officeCode" character varying, "attendanceStatus" character varying, message text)
 LANGUAGE plpgsql
AS $function$
declare
dynamicQuery varchar;
begin
return QUERY(select ea.checkin ,
        ea.checkout ,
        ea.date_en ,
        ea.date_np ,
        ea.pis_code,
        ea.office_code ,
        ea.attendance_status ,
        case when ea.attendance_status = 'LEAVE' then
                 (select case when lp.count_public_holiday = 'true' then 'सा.बि.' else case when ls.short_name_np is not null then ls.short_name_np else 'सा.बि.' end end
                  from leave_request lr
                           left join leave_request_detail lrd on lr.id = lrd.leave_request_id
                           left join leave_policy lp on lrd.leave_policy_id  = lp.id
                           left join leave_setup ls on lp.leave_setup_id = ls.id
                  where lr.emp_pis_code = piscode and lrd.status = 'A' and ea.date_en between lrd.from_date_en and lrd.to_date_en limit 1)
else case when ea.attendance_status = 'PUBLIC_HOLIDAY' then
(select
case when ph2.short_name_np is not null then ph2.short_name_np else 'सा. वि.' end
from periodic_holiday ph
left join public_holiday ph2 on ph.public_holiday_id = ph2.id
where ph2.office_code in (WITH recursive q AS (select o.parent_code, o.code
from office o
where o.code = ea.office_code
UNION ALL
select z.parent_code, z.code
from office z
join q on z.code = q.parent_code)
SELECT q.code from q )
and ea.date_en between  ph.from_date_en  and ph.to_date_en
)
else case when ea.attendance_status = 'KAAJ' then 'का'
else case when ea.attendance_status = 'UNINOFRMED_LEAVE_ABSENT' then 'अनु'
else case when ea.attendance_status = 'WEEKEND' then 'सा.बि.'
else case when ea.attendance_status = 'BAATO_MYAAD' then 'बा.म्या'
else '' end end end end end end as message
FROM employee_attendance ea
where ea.pis_code = piscode
and ea.date_en between (select min(eng_date)::date as fromDate from date_list where nepali_month= filter_month and nepali_year = filder_date)
and (select max(eng_date)::date as toDate from date_list where nepali_month= filter_month and nepali_year = filder_date)
order by ea.date_en);
END;
					$function$
;

