drop function if exists darta_master_count_office(character varying, character varying,character varying );
create function darta_master_count_office(fromdate character varying, todate character varying, lowerofficelist character varying)
    returns TABLE(officecode character varying, orderstatus text, manualcount bigint, autocount bigint, totaldarta bigint, chalanicount bigint, tippanicount bigint)
    language plpgsql
as
$$
declare
    dynamicQuery varchar;
BEGIN
    dynamicQuery :='
        (select dr.office_code as officeCode,
                ''DR'' order_status,
                case when(dr.manual is null) then 0 else dr.manual end,
                case when(dr.auto is null) then 0 else dr.auto end ,
                case when (dr.approvedDarta is null) then 0 else dr.approvedDarta end ,
                case when (c.approvedChalani is null) then 0 else c.approvedChalani end ,
                case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
         from
             (select d.office_code, d.approvedDarta::bigint,''dr'' as ordet_status,d.manual,d.auto from
                 (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                         sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                         count(*) as approvedDarta,
                         office_code
                  from received_letter rl
                  where
                      date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code =any('''||lowerofficelist||''')
                  group by office_code)d
              order by d.approvedDarta desc)as dr
                 left join (
                 select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                 where date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||''' and dl.sender_office_code=any('''||lowerofficelist||''')
                 group by dl.sender_office_code
             )as c on dr.office_code=c.sender_office_code
                 left join (
                 select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                     m.office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||''' and m.office_code=any('''||lowerofficelist||''')
                   and ma.is_active=true
                 group by m.office_code
             )as ti on ti.office_code=dr.office_code)
        union
        (
            select c.sender_office_code as officeCode,
                   ''CH'' order_status,
                   case when(dr.manual is null) then 0 else dr.manual end,
                   case when (dr.auto is null) then 0 else dr.auto end,
                   case when(dr.approvedDarta is null) then 0 else dr.approvedDarta end,
                   case when (c.approvedChalani is null)then 0 else c.approvedChalani end,
                   case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
            from
                (select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                 where
                     date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||''' and dl.sender_office_code=any('''||lowerofficelist||''')
                 group by dl.sender_office_code
                 order by approvedChalani desc
                )as c left join
                (select d.office_code, d.approvedDarta::bigint,''dr'' as ordet_status,d.manual,d.auto from
                    (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                            sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                            count(*) as approvedDarta,
                            office_code
                     from
                         received_letter rl
                     where
                         date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code=any('''||lowerofficelist||''')
                     group by office_code)d)as dr
                on c.sender_office_code=dr.office_code
                      left join (
                    select
                        sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                        m.office_code
                    from memo m
                             inner join memo_approval ma on m.id = ma.memo_id
                    where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||''' and m.office_code=any('''||lowerofficelist||''')
                      and ma.is_active=true
                    group by m.office_code
                )as ti on ti.office_code=dr.office_code
        )
        union (
            select ti.office_code as officeCode,
                   ''TI'' order_status,
                   case when(dr.manual is null) then 0 else dr.manual end,
                   case when (dr.auto is null) then 0 else dr.auto end,
                   case when(dr.approvedDarta is null) then 0 else dr.approvedDarta end,
                   case when (c.approvedChalani is null)then 0 else c.approvedChalani end,
                   case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
            from
                (select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi,
                     m.office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||''' and m.office_code=any('''||lowerofficelist||''')
                   and ma.is_active=true
                 group by m.office_code
                 order by approvedTippadi desc limit 10
                )as ti left join
                (select d.office_code, d.approvedDarta::bigint,''dr'' as ordet_status,d.manual,d.auto from
                    (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                            sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                            count(*) as approvedDarta,
                            office_code
                     from  received_letter rl
                     where
                         date(rl.created_date) between '''||fromdate||''' and '''||todate||''' and office_code=any('''||lowerofficelist||''')
                     group by office_code)d)as dr
                on ti.office_code=dr.office_code
                       left join (
                    select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani,
                           dl.sender_office_code
                    from dispatch_letter dl
                    where
                        date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||''' and dl.sender_office_code=any('''||lowerofficelist||''')
                    group by dl.sender_office_code
                )as c on ti.office_code=c.sender_office_code
        )';

    return QUERY execute dynamicQuery;

END;
$$;

alter function darta_master_count_office(varchar, varchar, varchar) owner to postgres;

select * from darta_master_count_office('2021-07-07','2022-07-07','{00}');


