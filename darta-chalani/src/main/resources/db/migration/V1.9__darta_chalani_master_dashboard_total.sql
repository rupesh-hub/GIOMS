
CREATE OR REPLACE FUNCTION darta_chalani_master_dashboard_total(fromdate character varying, todate character varying, lowerofficelist character varying)
 RETURNS TABLE(totaldarta numeric, autodarta numeric, manualdarta numeric, chalani numeric, tippani numeric)
 LANGUAGE plpgsql
AS $function$
declare
    dynamicQuery varchar;
BEGIN
    dynamicQuery :='select sum(approvedDarta) darta_number, sum(auto) auto_darta, sum(manual) manual_darta,   sum(approvedChalani) chalani, sum(approvedTippadi) tippani from
                  (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                         sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                         count(*) as approvedDarta,
                         office_code
                  from received_letter rl
                  where
                      date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code=any('''||lowerofficelist||''')
                  group by office_code) d
                  left join
                  (select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                 where date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                 group by dl.sender_office_code) c on d.office_code = c.sender_office_code
                 left join
                 (
                 select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                     m.office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                   and ma.is_active=true
                 group by m.office_code
             ) m on m.office_code = c.sender_office_code';

    return QUERY execute dynamicQuery;

END;
$function$
;