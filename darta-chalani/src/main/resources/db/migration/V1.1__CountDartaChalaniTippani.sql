drop function if exists count_darta_chalani_tippani(character varying, character varying,character varying);
create function count_darta_chalani_tippani(fromdate character varying, todate character varying, office character varying)
    returns TABLE(manualcount bigint, autocount bigint, totaldarta bigint, chalanicount bigint, tippanicount bigint)
    language plpgsql
as
$$
declare
    dynamicQuery varchar;
BEGIN
    dynamicQuery :=
        ' select
             case when(d.manual is null) then 0 else d.manual end,
             case when(d.auto is null) then 0 else d.auto end ,
             case when (d.approvedDarta is null) then 0 else d.approvedDarta end ,
             case when (c.approvedChalani is null) then 0 else c.approvedChalani end ,
             case when (ti.approvedTippadi is null) then 0 else ti.approvedTippadi end
         from

             (select sum(case when rl.entry_type =true then 1 else 0 end ) as manual,
                     sum(case when rl.entry_type =false then 1 else 0 end) as auto,
                     count(*) as approvedDarta
              from
                    received_letter rl
              where
                  date(rl.created_date) between '''||fromdate||''' and '''||todate||'''
                and office_code=any('''||office||'''))d,
             (
                 select sum(case when dl.status=''A'' then 1 else 0 end ) as approvedChalani

                 from dispatch_letter dl
                 where date(dl.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                   and dl.sender_office_code=any('''||office||''')
             )as c ,
             (
                 select
                     sum(case when ma.status=''A'' then 1 else 0 end ) as approvedTippadi
                 from memo m
                          inner join memo_approval ma on m.id = ma.memo_id
                 where date(ma.last_modified_date) between '''||fromdate||''' and '''||todate||'''
                   and
                         m.office_code=any('''||office||''') and ma.is_active=true
             )as ti';
    return QUERY execute dynamicQuery;
END;
$$;

alter function count_darta_chalani_tippani(varchar, varchar, varchar) owner to postgres;

