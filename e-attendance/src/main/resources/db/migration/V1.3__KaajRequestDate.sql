drop function if exists kaaj_request_dates(character  varying, character varying);
create function kaaj_request_dates(fromdate character varying, todate character varying)
    returns TABLE(leavecount bigint, officecode character varying, kaajcount bigint, orderstatus text)
    language plpgsql
as
$$
BEGIN
    return QUERY((select case when (lr.leave_count is null)then 0 else leave_count end as leave_count,
                         case when (lr.office_code is not null)then lr.office_code
                              else kr.office_code
                             end as office_code,
                         case when (kr.kaaj_count is null)then 0  else kr.kaaj_count end as kaaj_count,
                         'LR' as oder_status
                  from
                      (select count(distinct da.id) as leave_count,office_code
                       from decision_approval da
                                inner join leave_request_detail lrd on da.leave_request_detail_id = lrd.id
                                inner join leave_request lr on lrd.leave_request_id = lr.id
                       where lrd.is_active=true and lrd.status='A' and da.is_active=true
                         and date(da.last_modified_date) between date(fromDate) and date(toDate)
                       group by lr.office_code order by leave_count desc limit 10)as lr
                          left join
                      (select count(distinct da.id) as kaaj_count,office_code
                       from decision_approval da
                                inner join kaaj_request kr on da.kaaj_request_id = kr.id
                       where kr.is_active=true and kr.status='A' and da.is_active=true
                         and date(da.last_modified_date) between date(fromDate) and date(toDate)
                       group by kr.office_code)kr  on kr.office_code=lr.office_code)
                 union
                 (
                     select case when (l.leave_count is null) then 0 else leave_count end as leave_count,
                            case
                                when (k.office_code is not null) then k.office_code
                                else l.office_code
                                end                                                       as office_code,
                            case when (k.kaaj_count is null) then 0 else k.kaaj_count end as kaaj_count,
                            'KR'                                                          as oder_status
                     from (select d.office_code, d.kaaj_count
                           from (select count(distinct da.id) as kaaj_count, office_code
                                 from decision_approval da
                                          inner join kaaj_request kr on da.kaaj_request_id = kr.id
                                 where kr.is_active = true
                                   and kr.status = 'A'
                                   and da.is_active = true
                                   and date(da.last_modified_date) between date(fromDate) and date(toDate)
                                 group by kr.office_code) d
                           order by d.kaaj_count desc
                           limit 10) as k
                              left join
                          (select count(distinct da.id) as leave_count, office_code
                           from decision_approval da
                                    inner join leave_request_detail lrd on da.leave_request_detail_id = lrd.id
                                    inner join leave_request lr on lrd.leave_request_id = lr.id
                           where lrd.is_active = true
                             and lrd.status = 'A'
                             and da.is_active = true
                             and date(da.last_modified_date) between date(fromDate) and date(toDate)
                           group by lr.office_code) l on k.office_code = l.office_code
                 ));
END ;
$$;
alter function kaaj_request_dates(character varying , character varying ) owner to postgres;

