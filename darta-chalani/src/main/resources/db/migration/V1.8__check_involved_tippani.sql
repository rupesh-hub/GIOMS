CREATE OR REPLACE FUNCTION check_involved_tippani(
                                        v_id bigint,
                                        v_code varchar,
                                        pis_codes varchar
                                        )
returns boolean
AS
$$
declare
    dynamicQuery VARCHAR;
   total_count  int;
begin


    select count(tt.pis_code) into total_count
    from (
            select pis_code from memo m where m.id = v_id and m.section_code = v_code
union
select distinct approver_pis_code from memo_approval ma where ma.memo_id = v_id and ma.approver_section_code = v_code
union
select distinct approver_pis_code from memo_suggestion ms where ms.memo_id = v_id and ms.approver_section_code = v_code
union
select distinct sender_pis_code  from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = v_id)
and dl.sender_section_code = v_code
union
select dl.remarks_pis_code from dispatch_letter dl where dl.id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = v_id)
and remarks_pis_code is not null and dl.remarks_section_code = v_code
union
select distinct receiver_pis_code  from dispatch_letter_review dlr where dlr.dispatch_id in (select dispatch_id  from memo_reference mr where referenced_memo_id  = v_id)
and dlr.receiver_section_code = v_code
union
select distinct receiver_pis_code  from dispatch_letter_receiver_internal dlri where dlri.dispatch_letter_id in (select dispatch_id  from memo_reference mr
where referenced_memo_id  = v_id) and dlri.receiver_pis_code is not null and dlri.receiver_section_id = v_code
union
select distinct pis_code from memo m where m.id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = v_id) and m.section_code = v_code
union
select distinct approver_pis_code  from memo_approval ma where ma.memo_id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = v_id )
and ma.approver_section_code = v_code
union
select distinct approver_pis_code  from memo_suggestion ms where ms.memo_id in (select mr.memo_id  from memo_reference mr where referenced_memo_id  = v_id)
and ms.approver_section_code = v_code
union
select distinct receiver_pis_code  from received_letter_forward rlf2 where rlf2.received_letter_id in (select id  from received_letter rl where dispatch_id
in (select dispatch_id  from memo_reference mr where referenced_memo_id  = v_id)) and rlf2.receiver_section_id = v_code) tt
           join (select unnest(pis_codes :: varchar[]) pis_codes) t
          on t.pis_codes=tt.pis_code::varchar ;
    if total_count != 0 then
    begin
       return true;
      end;
     elsif total_count=0 then
    begin
        return false;
        end;
    end if;

end;
$$
LANGUAGE 'plpgsql';