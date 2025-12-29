-- Add CUSTOM report type to revenue_reports table check constraint
ALTER TABLE revenue_reports 
DROP CONSTRAINT revenue_reports_report_type_check;

ALTER TABLE revenue_reports 
ADD CONSTRAINT revenue_reports_report_type_check 
CHECK (report_type IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY', 'CUSTOM'));
