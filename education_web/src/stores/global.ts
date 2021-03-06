import {Subject} from 'rxjs';

export type GlobalState = {
  loading: boolean
  toast: {
    type: string
    message: string
  }
  dialog: {
    type: string
    message: string
    confirmText?: string
    cancelText?: string
  }
  uploadNotice: {
    type: string
    title: string
  },
  notice: {
    text: string
    reason: string
  },
  nativeWindowInfo: {
    visible: boolean
    items: any[]
  }
}

export class Root {
  private subject: Subject<GlobalState> | null;
  public state: GlobalState;
  public readonly defaultState: GlobalState = {
    loading: false,
    toast: {
      type: '',
      message: '',
    },
    dialog: {
      type: '',
      message: '',
      confirmText: 'confirm',
      cancelText: 'cancel'
    },
    uploadNotice: {
      type: '',
      title: '',
    },
    notice: {
      reason: '',
      text: '',
    },
    nativeWindowInfo: {
      visible: false,
      items: []
    }
  }

  constructor() {
    this.subject = null;
    this.state = this.defaultState;
  }

  initialize() {
    this.subject = new Subject<GlobalState>();
    this.state = {
      ...this.defaultState,
    }
    this.subject.next(this.state);
  }

  subscribe(updateState: any) {
    this.initialize();
    this.subject && this.subject.subscribe(updateState);
  }

  unsubscribe() {
    this.subject && this.subject.unsubscribe();
    this.subject = null;
  }

  commit (state: GlobalState) {
    this.subject && this.subject.next(state);
  }

  updateState(rootState: GlobalState) {
    this.state = {
      ...this.state,
      ...rootState,
    }
    this.commit(this.state);
  }

  showNotice({
    reason,
    text,
  }:{
    reason: string,
    text: string
  }) {
    this.state = {
      ...this.state,
      notice: {
        text,
        reason
      }
    }
    this.commit(this.state);
  }

  removeNotice() {
    this.state = {
      ...this.state,
      notice: {
        text: '',
        reason: ''
      }
    }
    this.commit(this.state);
  }

  setNativeWindowInfo({visible, items}: {visible: boolean, items: any[]}) {
    this.state = {
      ...this.state,
      nativeWindowInfo: {
        visible,
        items
      }
    }
    this.commit(this.state);
  }

  showUploadNotice({type, title}: {type: string, title: string}) {
    this.state = {
      ...this.state,
      uploadNotice: {
        type,
        title
      }
    }
    this.commit(this.state);
  }

  removeUploadNotice() {
    this.state = {
      ...this.state,
      uploadNotice: {
        type: '',
        title: ''
      }
    }
    this.commit(this.state);
  }

  showToast({type, message}: {type: string, message: string}) {
    this.state = {
      ...this.state,
      toast: {
        type, message
      },
    }
    this.commit(this.state);
  }

  showDialog({type, message}: {type: string, message: string}) {
    this.state = {
      ...this.state,
      dialog: {
        type,
        message
      },
    }
    this.commit(this.state);
  }

  removeDialog() {
    this.state = {
      ...this.state,
      dialog: {
        type: '',
        message: ''
      },
    }
    this.commit(this.state);
  }

  showLoading () {
    this.state = {
      ...this.state,
      loading: true
    }
    this.commit(this.state);
  }

  stopLoading () {
    this.state = {
      ...this.state,
      loading: false
    }
    this.commit(this.state);
  }
}

export const globalStore = new Root();