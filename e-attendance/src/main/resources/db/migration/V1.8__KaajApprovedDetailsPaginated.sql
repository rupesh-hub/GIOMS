drop function if exists kaaj_approved_details_paginated(character  varying, character varying,date,date,integer ,integer );
create function kaaj_approved_details_paginated(office_code character varying, pis_codes character varying, from_date_en date, to_date_en date, offsets integer, limits integer)
    returns TABLE(kaaj_approved_darta_no bigint, emp_name_en text, emp_name_np text, designation_name_en character varying, designation_name_np character varying, location character varying, advanced_amount_travel character varying, from_date_np character varying, to_date_np character varying, total_days integer, remarks_regarding_travel text, pis_code character varying, e_order integer, order_no bigint, current_position_app_date_bs character varying)
    language plpgsql
as
$$
declare

begin

    --if ($2 is not null and from_date_en is not null and  to_date_en is not null ) then
    return query
        select * from
            (select
                 kr.kaaj_approve_darta_no,
                 concat(e.first_name_en, ' ', e.middle_name_en, ' ', e.last_name_en)  as emp_name_en,
                 concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np) as emp_name_np,
                 fd.name_en as designation_name_en,
                 fd.name_np as designation_name_np,
                 kr.location,
                 kr.advance_amount_travel,
                 case
                     when kr.applied_for_others is false then kr.from_date_np
                     else krob.from_date_np
                     end as from_date_np,
                 case
                     when kr.applied_for_others is  false then kr.to_date_np
                     else krob.to_date_np
                     end as to_date_np,
                 case
                     when kr.applied_for_others is false then (kr.to_date_np::date-kr.from_date_np::date)+1
                     else
                             (krob.to_date_np::date-krob.from_date_np::date)+1
                     END AS total_days,
                 kr.remark_regarding_travel,
                 e.pis_code,
                 case
                     when e.employee_service_status_code = '01' then 0
                     when (e.employee_service_status_code = '04') then 2
                     when (e.employee_service_status_code = '08') then 3
                     when (e.employee_service_status_code = '09') then 4 else 0
                     end as e_order,
                 po.order_no,
                 e.current_position_app_date_bs
             from kaaj_request kr

                      left join kaaj_request_on_behalf krob
                                on kr.id=krob.kaaj_request_id

                      left join employee e
                                on(( e.pis_code = kr.pis_code and  kr.applied_for_others is false )
                                    or e.pis_code= krob.pis_code and kr.applied_for_others is true)

                      left join functional_designation fd
                                on e.designation_code = fd.code
                      left join users u on u.pis_employee_code = e.pis_code
                      left join employee_service_status es on es.code=e.employee_service_status_code
                      left join position po on e.position_code = po.code
             where kr.status ='A'
               and kr.office_code = $1
               and

                     (case when (kr.applied_for_others is true) then



                               (case when ($2 is not null and $3 is not null and $4 is not null) then

                                         (case when krob.pis_code = $2
                                             and ((krob.from_date_en between $3 and $4)
                                                 or (krob.to_date_en) between $3 and $4)
                                                   then 1 else 0 end
                                             )
                                     when ($2 is not null and $3 is null and $4 is  null) then
                                         (case when krob.pis_code = $2 then 1 else 0 end)

                                     when ($2 is null and $3 is not null and $4 is not null) then
                                         (case when ((krob.from_date_en between $3 and $4)
                                             or (krob.to_date_en) between $3 and $4)
                                                   then 1 else 0 end)

                                     else 1
                                   end)



                           else

                               (case when($2 is not null and $3 is not null and $4 is not null) then
                                         (case when kr.pis_code = $2
                                             and ((kr.from_date_en between $3 and $4)
                                                 or (kr.to_date_en) between $3 and $4)
                                                   then 1 else 0 end)

                                     when($2 is not null and $3 is null and $4 is  null) then
                                         (case when kr.pis_code = $2 then 1 else 0 end)

                                     when($2 is null and $3 is not null and $4 is not null) then
                                         (case when ((kr.from_date_en between $3 and $4)
                                             or (kr.to_date_en) between $3 and $4)
                                                   then 1 else 0 end)
                                     else 1

                                   end )
                         end)=1) as b order by b.e_order,b.order_no,b.current_position_app_date_bs,b.emp_name_en
        limit limits offset  offsets;
end;
$$;

alter function kaaj_approved_details_paginated(varchar, varchar, date, date, integer, integer) owner to postgres;

