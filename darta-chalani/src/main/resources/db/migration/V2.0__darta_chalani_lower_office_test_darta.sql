CREATE OR REPLACE FUNCTION public.darta_chalani_lower_office_test_darta(fromdate character varying, todate character varying, limits integer, offsets integer, lowerofficelist character varying, ordertype character varying)
 RETURNS TABLE(officecode character varying, orderstatus text, manualcount bigint, autocount bigint, totaldarta bigint, chalanicount bigint, tippanicount bigint)
 LANGUAGE plpgsql
AS $function$
declare
    dynamicQuery varchar;
BEGIN
    dynamicQuery :='
        (select q1.office_code as officeCode,
                ''DR'' order_status,
                case when(q1.manual is null) then 0 else q1.manual end,
                case when(q1.auto is null) then 0 else q1.auto end ,
                case when (q1.approvedDarta is null) then 0 else q1.approvedDarta end ,
                case when (q1.approvedChalani is null) then 0 else q1.approvedChalani end ,
                case when (q1.approvedTippadi is null) then 0 else q1.approvedTippadi end
         from
             ((select tt.office_code, coalesce (t.approvedDarta, 0) as approvedDarta, coalesce (t.manual, 0) as manual, coalesce (t.auto, 0) as auto from
                 (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                         sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                         count(*) as approvedDarta,
                         office_code
                  from received_letter rl
                  where
                      date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code=any('''||lowerOfficeList||''')
                  group by office_code)t
					right join (
            select unnest('''||lowerofficelist||''' :: varchar[]) as office_code) tt on t.office_code = tt.office_code
              )as dr
                 left join (
                 select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                 where date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||''' and dl.sender_office_code =any('''||lowerOfficeList||''')
                 group by dl.sender_office_code
             )as c on dr.office_code=c.sender_office_code
                 left join (
                 select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                     m.office_code as m_office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||''' and m.office_code =any('''||lowerOfficeList||''')
                   and ma.is_active=true
                 group by m.office_code
             )as ti on ti.m_office_code=dr.office_code ) q1 order by q1.approvedDarta '||ordertype||' limit '''||limits||''' offset '''||offsets||''')

        ';

    return QUERY execute dynamicQuery;

END;
$function$
;