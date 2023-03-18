import {Component, OnInit} from '@angular/core';
import {Message} from "../../dto/Message";
import {RequestService} from "../../services/request.service";

@Component({
  selector: 'app-task-for-repairs',
  templateUrl: './task-for-repairs.component.html',
  styleUrls: ['./task-for-repairs.component.scss']
})
export class TaskForRepairsComponent implements OnInit {

  constructor(public requestService: RequestService) {

  }

  messages: Message[] = []
  selectedMessage: Message;
  checked: boolean = false;

  ngOnInit(): void {

    setTimeout(() => {
      // @ts-ignore
      this.messages = JSON.parse(localStorage.getItem("MACHINES_BROKEN"));
    }, 1000);
  }

  async sendToRepair(message: Message) {
    this.messages = this.removeObjectWithId(message.id);
    await this.requestService.messageSetFrontSing([message.id])
    localStorage.setItem("MACHINES_BROKEN", JSON.stringify(this.messages))

  }

  removeObjectWithId(id: number) {
    const objWithIdIndex = this.messages.findIndex((obj) => obj.id === id);

    if (objWithIdIndex > -1) {
      this.messages.splice(objWithIdIndex, 1);
    }

    return this.messages;
  }

}
