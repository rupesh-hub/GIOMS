CREATE OR REPLACE FUNCTION public.darta_chalani_lower_office_test_tippani_excel(fromdate character varying, todate character varying, lowerofficelist character varying, ordertype character varying)
 RETURNS TABLE(officecode character varying, orderstatus text, manualcount bigint, autocount bigint, totaldarta bigint, chalanicount bigint, tippanicount bigint)
 LANGUAGE plpgsql
AS $function$
declare
    dynamicQuery varchar;
BEGIN
    dynamicQuery :='
			(
            select q3.m_office_code as officeCode,
                   ''TI'' order_status,
                   case when(q3.manual is null) then 0 else q3.manual end,
                   case when (q3.auto is null) then 0 else q3.auto end,
                   case when(q3.approvedDarta is null) then 0 else q3.approvedDarta end,
                   case when (q3.approvedChalani is null)then 0 else q3.approvedChalani end,
                   case when (q3.approvedTippadi is null) then 0 else q3.approvedTippadi end
            from
                ((select tt.office_code as m_office_code, coalesce(t.approvedTippadi, 0) as approvedTippadi from(select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                     m.office_code as m_office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||''' and m.office_code=any('''||lowerOfficeList||''')
                   and ma.is_active=true
                 group by m.office_code) t
					right join (select unnest('''||lowerofficelist||''' :: varchar[]) as office_code) tt on t.m_office_code = tt.office_code
                )as ti left join
                (select d.office_code, d.approvedDarta::bigint,''dr'' as ordet_status,d.manual,d.auto from
                    (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                            sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                            count(*) as approvedDarta,
                            office_code
                     from  received_letter rl
                     where
                         date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code=any('''||lowerOfficeList||''')
                     group by office_code)d)as dr
                on ti.m_office_code=dr.office_code
                       left join (
                    select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                           dl.sender_office_code
                    from dispatch_letter dl
                    where
                        date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||''' and dl.sender_office_code=any('''||lowerOfficeList||''')
                    group by dl.sender_office_code
                )as c on ti.m_office_code=c.sender_office_code) q3 order by q3.approvedTippadi '||ordertype||'
        )';

    return QUERY execute dynamicQuery;

END;
$function$
;