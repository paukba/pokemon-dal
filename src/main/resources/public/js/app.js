/* src/main/resources/public/js/app.js
   Externalized version of the original inline script in index.html.
   Provides the same global functions and UI wiring.
*/

(function () {
  "use strict";

  // --- Helpers (exposed where HTML expects them) ---
  window.baseUrl = function () {
    return (document.getElementById('baseUrl').value || '').replace(/\/$/, '');
  };

  window.formatMoney = function (v) {
    if (v === null || v === undefined) return '';
    const num = Number(v);
    if (isNaN(num)) return String(v);
    return '$' + num.toFixed(2);
  };

  window.escapeHtml = function (s) {
    return String(s || '').replace(/[&<>"'\/]/g, function (c) {
      return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;','/':'&#x2F;'}[c];
    });
  };

  window.handleError = function (e) {
    console.error(e);
    alert('Error: ' + (e.message || e));
  };

  function emptyRowHtml(colspan, text) {
    return `<tr class="empty-row"><td colspan="${colspan}" class="text-center">${escapeHtml(text)}</td></tr>`;
  }

  // --- Fetch & render functions (exposed globally where necessary) ---
  window.fetchOwners = async function () {
    const res = await fetch(baseUrl() + '/owners');
    if (!res.ok) throw new Error(await res.text());
    const arr = await res.json();
    const tbody = document.querySelector('#ownersTbl tbody');
    tbody.innerHTML = '';
    if (!arr || arr.length === 0) {
      tbody.innerHTML = emptyRowHtml(4, 'No owners found');
      return;
    }
    for (const o of arr) {
      const id = o.ownerId ?? o.owner_id;
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${id ?? ''}</td>
        <td>${escapeHtml(o.name || '')}</td>
        <td>${escapeHtml(o.email || '')}</td>
        <td>
          <button class="btn btn-sm btn-outline-info btn-compact" onclick="fetchOwnerValue(${id})">Total Value</button>
          <button class="btn btn-sm btn-outline-secondary btn-compact" onclick="showOwnerCollection(${id})">Show Collection</button>
        </td>`;
      tbody.appendChild(tr);
    }
  };

  window.fetchCards = async function () {
    const res = await fetch(baseUrl() + '/cards');
    if (!res.ok) throw new Error(await res.text());
    const arr = await res.json();
    const tbody = document.querySelector('#cardsTbl tbody');
    tbody.innerHTML = '';
    if (!arr || arr.length === 0) {
      tbody.innerHTML = emptyRowHtml(7, 'No cards found');
      return;
    }
    for (const c of arr) {
      const id = c.cardId ?? c.card_id;
      const val = c.marketValueUsd ?? c.market_value_usd;
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${id ?? ''}</td>
        <td>${escapeHtml(c.name || '')}</td>
        <td>${escapeHtml(c.cardSet || c.card_set || '')}</td>
        <td>${escapeHtml(c.rarity || '')}</td>
        <td>${escapeHtml(c.edition || '')}</td>
        <td>${formatMoney(val)}</td>
        <td>${escapeHtml(c.condition || '')}</td>
      `;
      tbody.appendChild(tr);
    }
  };

  window.fetchCollections = async function () {
    const res = await fetch(baseUrl() + '/collections');
    if (!res.ok) throw new Error(await res.text());
    const arr = await res.json();
    const tbody = document.querySelector('#collectionsTbl tbody');
    tbody.innerHTML = '';
    if (!arr || arr.length === 0) {
      tbody.innerHTML = emptyRowHtml(5, 'No collections found');
      return;
    }
    for (const e of arr) {
      const id = e.collectionId ?? e.collection_id;
      const acquired = e.acquiredDate ?? e.acquired_date;
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${id ?? ''}</td>
        <td>${e.ownerId ?? e.owner_id ?? ''}</td>
        <td>${e.cardId ?? e.card_id ?? ''}</td>
        <td>${e.quantity ?? ''}</td>
        <td>${acquired ?? ''}</td>
      `;
      tbody.appendChild(tr);
    }
  };

  window.fetchTrades = async function () {
    const res = await fetch(baseUrl() + '/trades');
    if (!res.ok) throw new Error(await res.text());
    const arr = await res.json();
    const tbody = document.querySelector('#tradesTbl tbody');
    tbody.innerHTML = '';
    if (!arr || arr.length === 0) {
      tbody.innerHTML = emptyRowHtml(9, 'No trades found');
      return;
    }
    for (const t of arr) {
      const id = t.tradeId ?? t.trade_id;
      const date = t.tradeDate ?? t.trade_date ?? '';
      const status = (t.status || '').toUpperCase();
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${id ?? ''}</td>
        <td>${t.fromOwner ?? t.from_owner ?? ''}</td>
        <td>${t.toOwner ?? t.to_owner ?? ''}</td>
        <td>${t.cardId ?? t.card_id ?? ''}</td>
        <td>${t.quantity ?? ''}</td>
        <td>${date}</td>
        <td>${escapeHtml(status)}</td>
        <td>${escapeHtml(t.notes || '')}</td>
        <td>${status === 'PROPOSED' ? '<button class="btn btn-sm btn-primary btn-compact" onclick="acceptTrade('+id+')">Accept</button>' : ''}</td>
      `;
      tbody.appendChild(tr);
    }
  };

  // --- Actions invoked by UI buttons ---
  window.fetchOwnerValue = async function (ownerId) {
    try {
      const res = await fetch(baseUrl() + '/owners/' + ownerId + '/value');
      if (!res.ok) throw new Error(await res.text());
      const v = await res.json();
      alert('Owner ' + ownerId + ' collection total value: ' + formatMoney(v));
    } catch (e) {
      handleError(e);
    }
  };

  window.showOwnerCollection = async function (ownerId) {
    try {
      const res = await fetch(baseUrl() + '/collections/owner/' + ownerId);
      if (!res.ok) throw new Error(await res.text());
      const arr = await res.json();
      let s = 'Collection for owner ' + ownerId + ':\n\n';
      for (const it of arr) {
        s += `collectionId=${it.collectionId ?? it.collection_id}, cardId=${it.cardId ?? it.card_id}, qty=${it.quantity}\n`;
      }
      alert(s);
    } catch (e) {
      handleError(e);
    }
  };

  window.acceptTrade = async function (tradeId) {
    if (!confirm('Accept trade #' + tradeId + '?')) return;
    try {
      const res = await fetch(baseUrl() + '/trades/' + tradeId + '/accept', { method: 'POST' });
      if (!res.ok) throw new Error(await res.text());
      const result = await res.json();
      alert((result.success ? 'OK: ' : 'FAIL: ') + result.message);
      await fetchTrades();
      await fetchCollections();
    } catch (e) {
      handleError(e);
    }
  };

  // --- Propose modal handlers (HTML references these globals) ---
  window.openPropose = function () {
    document.getElementById('p_from').value = '';
    document.getElementById('p_to').value = '';
    document.getElementById('p_card').value = '';
    document.getElementById('p_qty').value = '';
    document.getElementById('p_notes').value = '';
    document.getElementById('p_msg').innerText = '';
    // uses Bootstrap/jQuery to show the modal
    if (window.$ && typeof window.$ === 'function') {
      window.$('#proposeModal').modal('show');
    } else {
      // fallback: make the modal visible
      document.getElementById('proposeModal').style.display = 'block';
    }
  };

  window.closePropose = function () {
    if (window.$ && typeof window.$ === 'function') {
      window.$('#proposeModal').modal('hide');
    } else {
      document.getElementById('proposeModal').style.display = 'none';
    }
  };

  window.submitPropose = async function () {
    try {
      const from = parseInt(document.getElementById('p_from').value);
      const to = parseInt(document.getElementById('p_to').value);
      const card = parseInt(document.getElementById('p_card').value);
      const qty = parseInt(document.getElementById('p_qty').value);
      const notes = document.getElementById('p_notes').value || '';

      if (!Number.isInteger(from) || !Number.isInteger(to) || !Number.isInteger(card) || !Number.isInteger(qty)) {
        document.getElementById('p_msg').innerText = 'Please enter valid numeric IDs and quantity.';
        return;
      }

      const payload = {
        fromOwner: from,
        toOwner: to,
        cardId: card,
        quantity: qty,
        notes: notes
      };

      const res = await fetch(baseUrl() + '/trades/propose', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt);
      }
      const created = await res.json();
      alert('Trade proposed (id: ' + (created.tradeId ?? created.trade_id) + ')');
      closePropose();
      await fetchTrades();
    } catch (e) {
      document.getElementById('p_msg').innerText = e.message || e;
    }
  };

  // --- UI wiring (runs on DOMContentLoaded) ---
  document.addEventListener('DOMContentLoaded', function () {
    const fetchBtn = document.getElementById('fetchAllBtn');
    const clearBtn = document.getElementById('clearBtn');
    const openProposeBtn = document.getElementById('openProposeBtn');

    if (fetchBtn) {
      fetchBtn.addEventListener('click', async function () {
        try {
          fetchBtn.disabled = true;
          fetchBtn.innerText = 'Loading...';
          await window.fetchOwners();
          await window.fetchCards();
          await window.fetchCollections();
          await window.fetchTrades();
        } catch (e) {
          handleError(e);
        } finally {
          fetchBtn.disabled = false;
          fetchBtn.innerText = 'Fetch All';
        }
      });
    }

    if (clearBtn) {
      clearBtn.addEventListener('click', function () {
        document.querySelectorAll('table tbody').forEach(tb => (tb.innerHTML = ''));
      });
    }

    if (openProposeBtn) {
      openProposeBtn.addEventListener('click', window.openPropose);
    }

    // Auto-fetch shortly after load so the page populates
    setTimeout(() => {
      if (fetchBtn) fetchBtn.click();
    }, 250);
  });

})(); // end IIFE