document.addEventListener('deviceready', onDeviceReady, false);

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function onDeviceReady() {
  const noDataView = `<div id="noDataMessage" class="centered">No Notifications Found</div>`;

  let initItems = await NotificareInbox.getItems();
  if (initItems.length > 0) {
    initItems.forEach(createInboxItem);
  } else {
    document.getElementById('inboxList').innerHTML = noDataView;
  }

  NotificareInbox.onInboxUpdated((items) => {
    document.getElementById('inboxList').replaceChildren();

    if (items.length > 0) {
      items.forEach(createInboxItem);
    } else {
      document.getElementById('inboxList').innerHTML = noDataView;
    }
  });
}

async function open(item) {
  try {
    console.log(`---> Open Inbox Item CLicked <---`);
    const notification = await NotificareInbox.open(item);
    await NotificarePushUI.presentNotification(notification);
  } catch (e) {
    console.log(e);
  }
}

async function markAsRead(item) {
  try {
    console.log(`---> Mark as Read Inbox Item CLicked <---`);
    await NotificareInbox.markAsRead(item);
  } catch (e) {
    console.log(e);
  }
}

async function remove(item) {
  try {
    console.log(`---> Remove Inbox Item CLicked <---`);
    await NotificareInbox.remove(item);
  } catch (e) {
    console.log(e);
  }
}

function handleClick(element, item) {
  let timer;

  element.addEventListener('touchstart', () => {
    timer = setTimeout(() => {
      timer = null;

      document.getElementById('openItem').ontouchstart = function () {
        open(item);
      };

      document.getElementById('markAsReadItem').ontouchstart = function () {
        markAsRead(item);
      };

      document.getElementById('removeItem').ontouchstart = function () {
        remove(item);
      };

      document.getElementById('dropUpMenu').classList.add('dropup-show');
    }, 500);
  });

  element.addEventListener('touchend', function () {
    if (timer != null) {
      clearTimeout(timer);
      open(item);
    } else {
      document.body.ontouchstart = function () {
        document.body.ontouchstart = null;
        document.getElementById('dropUpMenu').classList.remove('dropup-show');
      };
    }
  });

  element.addEventListener('touchmove', function () {
    if (timer != null) {
      clearTimeout(timer);
    }
  });
}

function createInboxItem(item) {
  const image = item.notification.attachments[0] != null ? item.notification.attachments[0].uri : '../../res/logo.png';

  const itemView = `<div class="container">
  <div class="attachment-container">
   <img class="attachment-image" src="${image}" alt="Application logo"/>
  </div>

  <div class="details-container">
      <div class="time">${item.time}</div>
    <span class="title">${item.notification.title ?? '---'}</span>
    <span class="message">${item.notification.message}</span>
  </div>

  <div class="unread-container">
    <div class="unread-indicator  ${!item.opened ? 'visible' : 'hidden'}" />
  </div>
</div>`;

  const lineView = `<div class="line" />`;

  const itemElement = document.createElement('div');
  itemElement.innerHTML = itemView;

  const lineElement = document.createElement('div');
  lineElement.innerHTML = lineView;

  handleClick(itemElement, item);

  if (document.getElementById('inboxList').children.length > 0) {
    document.getElementById('inboxList').appendChild(lineElement);
  }

  document.getElementById('inboxList').appendChild(itemElement);
}
