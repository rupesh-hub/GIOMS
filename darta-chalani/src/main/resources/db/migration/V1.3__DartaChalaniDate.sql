drop function if exists darta_chalani_date(character varying, character varying);
create function darta_chalani_date(fromdate character varying, todate character varying)
    returns TABLE(officecode character varying, orderstatus text, manualcount bigint, autocount bigint, totaldarta bigint, chalanicount bigint, tippanicount bigint)
    language plpgsql
as
$$
BEGIN

    return QUERY(
        (select dr.office_code as officeCode,
                'DR' order_status,
                case when(dr.manual is null) then 0 else dr.manual end,
                case when(dr.auto is null) then 0 else dr.auto end ,
                case when (dr.approvedDarta is null) then 0 else dr.approvedDarta end ,
                case when (c.approvedChalani is null) then 0 else c.approvedChalani end ,
                case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
         from
             (select d.office_code, d.approvedDarta::bigint,'dr' as ordet_status,d.manual,d.auto from
                 (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                         sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                         count(*) as approvedDarta,
                         office_code
                  from received_letter_forward rlf
                           inner join received_letter rl on rlf.received_letter_id = rl.id
                  where
                      date(rl.created_date) between date(fromdate) and date(todate)
                    and rlf.completion_status='FN'
                  group by office_code)d
              order by d.approvedDarta desc limit 10)as dr
                 left join (
                 select sum(case when dl.status='A' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                          inner join dispatch_letter_review dlv on dlv.dispatch_id =dl.id
                 where date(dl.last_modified_date) between date(fromdate) and date(todate)

                 group by dl.sender_office_code
             )as c on dr.office_code=c.sender_office_code
                 left join (
                 select
                     sum(case when ma.status='A' then 1 else 0 end ) as approvedTippadi,
                     m.office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between date(fromdate) and date(todate)
                 group by m.office_code
             )as ti on ti.office_code=dr.office_code)
        union
        (
            select c.sender_office_code as officeCode,
                   'CH' order_status,
                   case when(dr.manual is null) then 0 else dr.manual end,
                   case when (dr.auto is null) then 0 else dr.auto end,
                   case when(dr.approvedDarta is null) then 0 else dr.approvedDarta end,
                   case when (c.approvedChalani is null)then 0 else c.approvedChalani end,
                   case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
            from
                (select sum(case when dl.status='A' then 1 else 0 end ) as approvedChalani,
                        dl.sender_office_code
                 from dispatch_letter dl
                          inner join dispatch_letter_review dlv on dlv.dispatch_id =dl.id
                 where
                     date(dl.last_modified_date) between date(fromdate) and date(todate)
                 group by dl.sender_office_code
                 order by approvedChalani desc limit 10
                )as c left join
                (select d.office_code, d.approvedDarta::bigint,'dr' as ordet_status,d.manual,d.auto from
                    (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                            sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                            count(*) as approvedDarta,
                            office_code
                     from received_letter_forward rlf
                              inner join received_letter rl on rlf.received_letter_id = rl.id
                     where
                         date(rl.created_date) between date(fromdate) and date(todate)
                       and rlf.completion_status='FN'
                     group by office_code)d)as dr
                on c.sender_office_code=dr.office_code
                      left join (
                    select
                        sum(case when ma.status='A' then 1 else 0 end ) as approvedTippadi,
                        m.office_code
                    from memo m
                             inner join memo_approval ma on m.id = ma.memo_id
                    where date(ma.last_modified_date) between date(fromdate) and date(todate)
                    group by m.office_code
                )as ti on ti.office_code=dr.office_code
        )
        union (
            select ti.office_code as officeCode,
                   'TI' order_status,
                   case when(dr.manual is null) then 0 else dr.manual end,
                   case when (dr.auto is null) then 0 else dr.auto end,
                   case when(dr.approvedDarta is null) then 0 else dr.approvedDarta end,
                   case when (c.approvedChalani is null)then 0 else c.approvedChalani end,
                   case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
            from
                (select
                     sum(case when ma.status='A' then 1 else 0 end ) as approvedTippadi,
                     m.office_code
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between date(fromdate) and date(todate)
                 group by m.office_code
                 order by approvedTippadi desc limit 10
                )as ti left join
                (select d.office_code, d.approvedDarta::bigint,'dr' as ordet_status,d.manual,d.auto from
                    (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                            sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                            count(*) as approvedDarta,
                            office_code
                     from received_letter_forward rlf
                              inner join received_letter rl on rlf.received_letter_id = rl.id
                     where
                         date(rl.created_date) between date(fromdate) and date(todate)
                       and rlf.completion_status='FN'
                     group by office_code)d)as dr
                on ti.office_code=dr.office_code
                       left join (
                    select sum(case when dl.status='A' then 1 else 0 end ) as approvedChalani,
                           dl.sender_office_code
                    from dispatch_letter dl
                             inner join dispatch_letter_review dlv on dlv.dispatch_id =dl.id
                    where
                        date(dl.last_modified_date) between date(fromdate) and date(todate)
                    group by dl.sender_office_code
                )as c on ti.office_code=c.sender_office_code
        )
    );

END;
$$;

alter function darta_chalani_date(varchar, varchar) owner to postgres;

