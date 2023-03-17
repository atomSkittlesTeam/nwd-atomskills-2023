export class Process {
    id: number;
    /* to join with business entity */
    businessKey: number;

    /* foreign key for user */
    userLogin: string;

    processStartDate: Date;
    processEndDate: Date;

    /* foreign key for ProcessStage */
    currentStageId: number;
}